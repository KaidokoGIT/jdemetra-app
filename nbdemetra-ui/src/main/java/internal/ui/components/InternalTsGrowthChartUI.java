/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package internal.ui.components;

import demetra.ui.components.TsSelectionBridge;
import demetra.ui.components.HasChart;
import demetra.ui.components.HasChart.LinesThickness;
import demetra.ui.components.HasColorScheme;
import demetra.ui.components.HasObsFormat;
import demetra.ui.components.HasTsCollection;
import static demetra.ui.components.PrintableWithPreview.PRINT_ACTION;
import static demetra.ui.components.ResetableZoom.RESET_ZOOM_ACTION;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.ThemeSupport;
import demetra.ui.util.ActionMaps;
import demetra.ui.util.InputMaps;
import demetra.ui.components.JTsGrowthChart;
import demetra.ui.actions.Actions;
import ec.ui.chart.JTimeSeriesChartUtil;
import ec.ui.chart.TsXYDatasets;
import ec.util.chart.ObsFunction;
import ec.util.chart.SeriesFunction;
import ec.util.chart.SeriesPredicate;
import ec.util.chart.TimeSeriesChart;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.chart.swing.SelectionMouseListener;
import ec.util.list.swing.JLists;
import ec.util.various.swing.JCommand;
import static internal.ui.components.JTsGrowthChartCommands.PREVIOUS_PERIOD_ACTION;
import static internal.ui.components.JTsGrowthChartCommands.PREVIOUS_YEAR_ACTION;
import static internal.ui.components.JTsGrowthChartCommands.applyGrowthKind;
import static internal.ui.components.JTsGrowthChartCommands.copyGrowthData;
import static internal.ui.components.JTsGrowthChartCommands.editLastYears;
import java.awt.BorderLayout;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.*;
import org.jfree.data.xy.IntervalXYDataset;
import demetra.ui.datatransfer.DataTransfer;

/**
 *
 * @author Kristof Bayens
 */
public final class InternalTsGrowthChartUI implements InternalUI<JTsGrowthChart> {

    private JTsGrowthChart target;

    private final JTimeSeriesChart chartPanel = new JTimeSeriesChart();
    private final ListSelectionModel selectionModel = new DefaultListSelectionModel();
    private final ThemeSupport themeSupport = ThemeSupport.registered();

    private InternalTsSelectionAdapter selectionListener;

    @Override
    public void install(JTsGrowthChart component) {
        this.target = component;

        this.selectionListener = new InternalTsSelectionAdapter(target);

        themeSupport.setColorSchemeListener(target, this::onColorSchemeChange);
        themeSupport.setObsFormatListener(target, this::onDataFormatChange);

        registerActions();
        registerInputs();

        initChart();

        enableSeriesSelection();
        enableDropPreview();
        enableOpenOnDoubleClick();
        enableProperties();

        target.setLayout(new BorderLayout());
        target.add(chartPanel, BorderLayout.CENTER);
    }

    private void registerActions() {
        ActionMap am = target.getActionMap();
        HasChartCommands.registerActions(target, am);
        am.put(PREVIOUS_PERIOD_ACTION, applyGrowthKind(JTsGrowthChart.GrowthKind.PreviousPeriod).toAction(target));
        am.put(PREVIOUS_YEAR_ACTION, applyGrowthKind(JTsGrowthChart.GrowthKind.PreviousYear).toAction(target));
        am.put(HasObsFormatCommands.FORMAT_ACTION, HasObsFormatCommands.editDataFormat().toAction(target));
        HasTsCollectionCommands.registerActions(target, target.getActionMap());
        HasChartCommands.registerActions(target, target.getActionMap());
        target.getActionMap().put(HasObsFormatCommands.FORMAT_ACTION, HasObsFormatCommands.editDataFormat().toAction(target));
        target.getActionMap().put(PRINT_ACTION, JCommand.of(JTimeSeriesChartUtil::printWithPreview).toAction(chartPanel));
        target.getActionMap().put(RESET_ZOOM_ACTION, JCommand.of(JTimeSeriesChart::resetZoom).toAction(chartPanel));
        ActionMaps.copyEntries(target.getActionMap(), false, chartPanel.getActionMap());
    }

    private void registerInputs() {
        HasTsCollectionCommands.registerInputs(target.getInputMap());
        InputMaps.copyEntries(target.getInputMap(), false, chartPanel.getInputMap());
    }

    private void initChart() {
        onAxisVisibleChange();
        onColorSchemeChange();
        onLegendVisibleChange();
        onTitleChange();
        onUpdateModeChange();
        onDataFormatChange();
        onTransferHandlerChange();
        onComponentPopupMenuChange();
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(1);
        chartPanel.setValueFormat(percent);
        chartPanel.setSeriesFormatter(new SeriesFunction<String>() {
            @Override
            public String apply(int series) {
                return target.getTsCollection().getData().size() > series
                        ? target.getTsCollection().getData().get(series).getName()
                        : chartPanel.getDataset().getSeriesKey(series).toString();
            }
        });
        chartPanel.setObsFormatter(new ObsFunction<String>() {
            @Override
            public String apply(int series, int obs) {
                IntervalXYDataset dataset = chartPanel.getDataset();
                CharSequence period = chartPanel.getPeriodFormat().format(new Date(dataset.getX(series, obs).longValue()));
                CharSequence value = chartPanel.getValueFormat().format(dataset.getY(series, obs));
                StringBuilder result = new StringBuilder();
                result.append(period).append(": ").append(value);
                return result.toString();
            }
        });
        chartPanel.setLegendVisibilityPredicate(new SeriesPredicate() {
            @Override
            public boolean apply(int series) {
                return series < target.getTsCollection().getData().size();
            }
        });
        chartPanel.setSeriesRenderer(SeriesFunction.always(TimeSeriesChart.RendererType.COLUMN));
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableSeriesSelection() {
        selectionModel.addListSelectionListener(selectionListener);
        chartPanel.addMouseListener(new SelectionMouseListener(selectionModel, true));
    }

    private void enableDropPreview() {
        new HasTsCollectionDropTargetListener(target, DataTransfer.getDefault())
                .register(chartPanel.getDropTarget());
    }

    private void enableOpenOnDoubleClick() {
        chartPanel.addMouseListener(new OpenOnDoubleClick(target.getActionMap()));
    }

    private void enableProperties() {
        target.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case HasTsCollection.TS_COLLECTION_PROPERTY:
                    onCollectionChange();
                    break;
                case TsSelectionBridge.TS_SELECTION_PROPERTY:
                    onSelectionChange();
                    break;
                case HasTsCollection.UDPATE_MODE_PROPERTY:
                    onUpdateModeChange();
                    break;
                case HasColorScheme.COLOR_SCHEME_PROPERTY:
                    onColorSchemeChange();
                    break;
                case HasObsFormat.DATA_FORMAT_PROPERTY:
                    onDataFormatChange();
                    break;
                case HasChart.LEGEND_VISIBLE_PROPERTY:
                    onLegendVisibleChange();
                    break;
                case HasChart.TITLE_VISIBLE_PROPERTY:
                    onTitleVisibleChange();
                    break;
                case HasChart.AXIS_VISIBLE_PROPERTY:
                    onAxisVisibleChange();
                    break;
                case HasChart.TITLE_PROPERTY:
                    onTitleChange();
                    break;
                case HasChart.LINES_THICKNESS_PROPERTY:
                    onLinesThicknessChange();
                    break;
                case JTsGrowthChart.GROWTH_KIND_PROPERTY:
                    onGrowthKindChange();
                    break;
                case JTsGrowthChart.LAST_YEARS_PROPERTY:
                    onLastYearsChange();
                    break;
                case "transferHandler":
                    onTransferHandlerChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    private void onDataFormatChange() {
        try {
            chartPanel.setPeriodFormat(themeSupport.getDataFormat().newDateFormat());
        } catch (IllegalArgumentException ex) {
            // do nothing?
        }
    }

    private void onColorSchemeChange() {
        chartPanel.setColorSchemeSupport(null);
        chartPanel.setColorSchemeSupport(themeSupport);
    }

    private void onCollectionChange() {
        chartPanel.setDataset(TsXYDatasets.from(target.computeGrowthData()));
        chartPanel.resetZoom();
//        refreshRange(plot);
    }

    /**
     * Redraws all the curves from the chart; unlike redrawAll() it won't cause
     * the chart to lose its zoom level.
     */
    private void onSelectionChange() {
        selectionListener.setEnabled(false);
        selectionModel.clearSelection();
        JLists.addSelectionIndexStream(selectionModel, target.getTsSelectionIndexStream());
        selectionListener.setEnabled(true);
    }

    private void onUpdateModeChange() {
        chartPanel.setNoDataMessage(target.getTsUpdateMode().isReadOnly() ? "No data" : "Drop data here");
    }

    private void onLegendVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.LEGEND, target.isLegendVisible());
    }

    private void onTitleVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.TITLE, target.isTitleVisible());
    }

    private void onAxisVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.AXIS, target.isAxisVisible());
    }

    private void onTitleChange() {
        chartPanel.setTitle(target.getTitle());
    }

    private void onLinesThicknessChange() {
        chartPanel.setLineThickness(target.getLinesThickness() == LinesThickness.Thin ? 1f : 2f);
    }

    private void onGrowthKindChange() {
        onCollectionChange();
    }

    private void onLastYearsChange() {
        onCollectionChange();
    }

    private void onTransferHandlerChange() {
        TransferHandler th = target.getTransferHandler();
        chartPanel.setTransferHandler(th != null ? th : new HasTsCollectionTransferHandler(target, DataTransfer.getDefault()));
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = target.getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildChartMenu().getPopupMenu());
    }
    //</editor-fold>

    private JMenu buildKindMenu() {
        ActionMap am = target.getActionMap();
        JMenu result = new JMenu("Kind");

        JMenuItem item;

        item = new JCheckBoxMenuItem(am.get(PREVIOUS_PERIOD_ACTION));
        item.setText("Previous Period");
        result.add(item);

        item = new JCheckBoxMenuItem(am.get(PREVIOUS_YEAR_ACTION));
        item.setText("Previous Year");
        result.add(item);

        return result;
    }

    private JMenu buildExportImageMenu(DemetraUI demetraUI) {
        JMenu result = new JMenu("Export image to");
        result.add(InternalComponents.menuItemOf(target));
        result.add(InternalComponents.newCopyImageMenu(chartPanel, demetraUI));
        result.add(InternalComponents.newSaveImageMenu(chartPanel, demetraUI));
        return result;
    }

    private JMenu buildMenu(DemetraUI demetraUI) {
        ActionMap am = target.getActionMap();
        JMenu result = new JMenu();

        result.add(HasTsCollectionCommands.newOpenMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newOpenWithMenu(target, demetraUI));

        JMenu menu = HasTsCollectionCommands.newSaveMenu(target, demetraUI);
        if (menu.getSubElements().length > 0) {
            result.add(menu);
        }

        result.add(HasTsCollectionCommands.newRenameMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newFreezeMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newCopyMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newPasteMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newDeleteMenu(am, demetraUI));
        result.addSeparator();
        result.add(HasTsCollectionCommands.newSelectAllMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newClearMenu(am, demetraUI));

        return result;
    }

    private JMenu buildChartMenu() {
        DemetraUI demetraUI = DemetraUI.getDefault();

        ActionMap am = target.getActionMap();
        JMenu result = buildMenu(demetraUI);

        JMenuItem item;

        result.add(HasTsCollectionCommands.newSplitMenu(am, demetraUI));
        result.addSeparator();
        result.add(HasChartCommands.newToggleTitleVisibilityMenu(am, demetraUI));
        result.add(HasChartCommands.newToggleLegendVisibilityMenu(am, demetraUI));
        result.add(HasObsFormatCommands.newEditFormatMenu(am, demetraUI));
        result.add(HasColorSchemeCommands.menuOf(target, demetraUI.getColorSchemes()));
        result.add(InternalComponents.newResetZoomMenu(am, demetraUI));

        result.add(buildExportImageMenu(demetraUI));

        // NEXT
        item = new JMenuItem(copyGrowthData().toAction(target));
        item.setText("Copy growth data");
        Actions.hideWhenDisabled(item);
        result.add(item);

        result.add(buildKindMenu());

        item = new JMenuItem(editLastYears().toAction(target));
        item.setText("Edit last years...");
        result.add(item);

        return result;
    }
}

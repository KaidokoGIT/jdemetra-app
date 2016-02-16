/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tools;

import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.IActiveView;
import ec.nbdemetra.ui.tsaction.ITsView2;
import ec.tss.Ts;
import ec.tstoolkit.data.WindowType;
import ec.ui.view.TukeySpectrumView;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JMenu;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//ec.nbdemetra.ui.tools//TukeySpectrum//EN",
autostore = false)
@TopComponent.Description(preferredID = "TukeySpectrumTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "ec.nbdemetra.ui.tools.TukeySpectrumTopComponent")
@ActionReference(path = "Menu/Tools/Spectral analysis", position = 300)
@TopComponent.OpenActionRegistration(displayName = "#CTL_TukeySpectrumAction")
@Messages({
    "CTL_TukeySpectrumAction=Tukey Spectrum",
    "CTL_TukeySpectrumTopComponent=Tukey Spectrum Window",
    "HINT_TukeySpectrumTopComponent=This is a Tukey Spectrum window"
})
public final class TukeySpectrumTopComponent extends TopComponent implements ITsView2, IActiveView, ExplorerManager.Provider {

    private TukeySpectrumView view;
    private Node node;

    public TukeySpectrumTopComponent() {
        initComponents();
        view = new TukeySpectrumView();
        add(view);
        setName(Bundle.CTL_TukeySpectrumTopComponent());
        setToolTipText(Bundle.HINT_TukeySpectrumTopComponent());
        node = new InternalNode();
        associateLookup(ExplorerUtils.createLookup(ActiveViewManager.getInstance().getExplorerManager(), getActionMap()));

    }

    @Override
    public void open() {
        super.open();
        Mode mode = WindowManager.getDefault().findMode("output");
        if (mode != null && mode.canDock(this)) {
            mode.dockInto(this);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    @Override
    public void componentActivated() {
        ActiveViewManager.getInstance().set(this);
    }

    @Override
    public void componentDeactivated() {
        ActiveViewManager.getInstance().set(null);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public boolean fill(JMenu menu) {
        return false;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return ActiveViewManager.getInstance().getExplorerManager();
    }

    @Override
    public boolean hasContextMenu() {
        return false;
    }

    @Override
    public Ts getTs() {
        return null;
    }

    @Override
    public void setTs(Ts ts) {
        view.setTs(ts);
    }

    class InternalNode extends AbstractNode {

        @Messages({
            "turkeySpectrumTopComponent.internalNode.displayName=Tukey Spectrum"
        })
        InternalNode() {
            super(Children.LEAF);
            setDisplayName(Bundle.turkeySpectrumTopComponent_internalNode_displayName());
        }

        @Override
        @Messages({
            "turkeySpectrumTopComponent.transform.name=Transform",
            "turkeySpectrumTopComponent.transform.displayName=Transformation",
            "turkeySpectrumTopComponent.log.name=Log",
            "turkeySpectrumTopComponent.log.desc=when marked, logarithmic transformation is used.",
            "turkeySpectrumTopComponent.differencing.name=Differencing",
            "turkeySpectrumTopComponent.differencing.desc=An order of a regular differencing of the series.",
            "turkeySpectrumTopComponent.differencingLag.name=Differencing lag",
            "turkeySpectrumTopComponent.differencingLag.desc=A number of lags used to take differences. For example, if Differencing lag = 3 then the differencing filter does not apply to the first lag (default) but to the third lag.",
            "turkeySpectrumTopComponent.lastYears.name=Last years",
            "turkeySpectrumTopComponent.lastYears.desc=A number of years of observations at the end of the time series used to produce the autoregressive spectrum (0=the whole time series is considered.",
            "turkeySpectrumTopComponent.turkeySpectrum.name=Tukey Spectrum",
            "turkeySpectrumTopComponent.turkeySpectrum.displayName=Tukey Spectrum",
            "turkeySpectrumTopComponent.taperPart.name=Taper part",
            "turkeySpectrumTopComponent.taperPart.desc=A parameter larger than 0 and smaller or equal to one that shapes the curvature of the smoothing function that is applied to the auto-covariance function.",
            "turkeySpectrumTopComponent.windowLenght.name=Window length",
            "turkeySpectrumTopComponent.windowLenght.desc=The size of the window that is used to smooth the auto-covariance function. The value zero considers the whole series.",
            "turkeySpectrumTopComponent.windowType.name=Window type",
            "turkeySpectrumTopComponent.windowType.desc=It refers to the weighting scheme that it is used to smooth the auto-covariance function. The available windows types (Square, Welch, Tukey, Barlett, Hamming, Parzen) are suitable to estimate the spectral density."
        })
        protected Sheet createSheet() {
            Sheet sheet= super.createSheet();
            Set transform = Sheet.createPropertiesSet();
            transform.setName(Bundle.turkeySpectrumTopComponent_transform_name());
            transform.setDisplayName(Bundle.turkeySpectrumTopComponent_transform_displayName());
            Property<Boolean> log = new Property(Boolean.class) {

                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return view.isLogTransformation();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    view.setLogTransformation((Boolean) t);
                }
            };

            log.setName(Bundle.turkeySpectrumTopComponent_log_name());
            log.setShortDescription(Bundle.turkeySpectrumTopComponent_log_desc());
            transform.put(log);
            Property<Integer> diff = new Property(Integer.class) {

                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return view.getDifferencingOrder();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    view.setDifferencingOrder((Integer) t);
                }
            };

            diff.setName(Bundle.turkeySpectrumTopComponent_differencing_name());
            diff.setShortDescription(Bundle.turkeySpectrumTopComponent_differencing_desc());
            transform.put(diff);

            Node.Property<Integer> diffLag = new Node.Property(Integer.class) {

                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return view.getDifferencingLag();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    view.setDifferencingLag((Integer) t);
                }
            };
            diffLag.setName(Bundle.turkeySpectrumTopComponent_differencingLag_name());
            diffLag.setShortDescription(Bundle.turkeySpectrumTopComponent_differencingLag_desc());
            transform.put(diffLag);

            Node.Property<Integer> length = new Node.Property(Integer.class) {

                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return view.getLastYears();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    view.setLastYears((Integer) t);
                }
            };
            length.setName(Bundle.turkeySpectrumTopComponent_lastYears_name());
            length.setShortDescription(Bundle.turkeySpectrumTopComponent_lastYears_desc());
            transform.put(length);

            sheet.put(transform);
            Set spectrum = Sheet.createPropertiesSet();
            spectrum.setName(Bundle.turkeySpectrumTopComponent_turkeySpectrum_name());
            spectrum.setDisplayName(Bundle.turkeySpectrumTopComponent_turkeySpectrum_displayName());
            Property<Double> taper = new Property(Double.class) {

                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return view.getTaperPart();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    double p = (Double) t;
                    if (p < 0 || p > 1) {
                        throw new IllegalArgumentException("Should be in [0,1]");
                    }
                    view.setTaperPart(p);
                }
            };
            taper.setName(Bundle.turkeySpectrumTopComponent_taperPart_name());
            taper.setShortDescription(Bundle.turkeySpectrumTopComponent_taperPart_desc());
            spectrum.put(taper);
            Property<Integer> wlength = new Property(Integer.class) {

                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return view.getWindowLength();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    view.setWindowLength((Integer) t);
                }
            };

            wlength.setName(Bundle.turkeySpectrumTopComponent_windowLenght_name());
            wlength.setShortDescription(Bundle.turkeySpectrumTopComponent_windowLenght_desc());
            spectrum.put(wlength);
            Property<WindowType> wtype = new Property(WindowType.class) {

                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return view.getWindowType();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    view.setWindowType((WindowType) t);
                }
            };

            wtype.setName(Bundle.turkeySpectrumTopComponent_windowType_name());
            wtype.setShortDescription(Bundle.turkeySpectrumTopComponent_windowType_desc());
            spectrum.put(wtype);
            sheet.put(spectrum);
            return sheet;
        }
    }
}

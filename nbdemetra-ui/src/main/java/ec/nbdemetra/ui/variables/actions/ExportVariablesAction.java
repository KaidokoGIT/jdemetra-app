/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.nbdemetra.ui.variables.actions;

import com.google.common.base.Converter;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.ExportAction;
import ec.nbdemetra.ui.interchange.Exportable;
import ec.nbdemetra.ui.nodes.Nodes;
import ec.nbdemetra.ui.variables.VariablesDocumentManager;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.timeseries.regression.TsVariables;
import java.util.List;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "ec.nbdemetra.ui.variables.actions.ExportVariablesAction")
@ActionRegistration(displayName = "#CTL_ExportVariablesAction", lazy = false)
@ActionReferences({
    @ActionReference(path = VariablesDocumentManager.ITEMPATH, position = 1430)
})
@Messages("CTL_ExportVariablesAction=Export to")
public final class ExportVariablesAction extends NodeAction implements Presenter.Popup {

    private static final Converter<TsVariables, Config> CONVERTER = new VariablesConfig();

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = ExportAction.getPopupPresenter(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportVariablesAction());
        return result;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return !Nodes.asIterable(activatedNodes)
                .filter(ItemWsNode.class)
                .filter(ExportVariablesAction::isExportable)
                .isEmpty();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    private static boolean isExportable(ItemWsNode o) {
        return o.getItem().getElement() instanceof TsVariables;
    }

    private static List<Exportable> getExportables(Node[] activatedNodes) {
        return Nodes.asIterable(activatedNodes)
                .filter(ItemWsNode.class)
                .filter(ExportVariablesAction::isExportable)
                .transform(o -> (Exportable) new ExportableVariables(o))
                .toList();
    }

    private static final class ExportableVariables implements Exportable {

        private final ItemWsNode input;

        public ExportableVariables(ItemWsNode input) {
            this.input = input;
        }

        @Override
        public Config exportConfig() {
            TsVariables value = (TsVariables) input.getItem().getElement();
            return CONVERTER.convert(value);
        }
    }
}

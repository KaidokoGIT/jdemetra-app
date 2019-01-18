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
package ec.nbdemetra.ui.ns;

import com.google.common.collect.Iterables;
import demetra.ui.NamedService;
import ec.nbdemetra.ui.nodes.AbstractNodeBuilder;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import javax.swing.JComboBox;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ChoiceView;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Philippe Charles
 */
public class NamedServiceChoicePanel extends javax.swing.JPanel implements ExplorerManager.Provider {

    final ExplorerManager em;

    /**
     * Creates new form TsActionChoicePanel
     */
    public NamedServiceChoicePanel() {
        initComponents();

        em = new ExplorerManager();
        jComboBox1.setPreferredSize(new Dimension(200, 24));
    }

    public void setContent(Iterable<? extends NamedService> namedServices) {
        Iterable<NamedServiceNode> nodes = Iterables.transform(namedServices, o -> new NamedServiceNode(o));
        em.setRootContext(new AbstractNodeBuilder().add(nodes).orderable(false).build());
    }

    public String getSelectedServiceName() {
        Node[] nodes = em.getSelectedNodes();
        return nodes.length == 0 ? null : nodes[0].getName();
    }

    public void setSelectedServiceName(String name) {
        Node node = em.getRootContext().getChildren().findChild(name);
        if (node != null) {
            try {
                em.setSelectedNodes(new Node[]{node});
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new ChoiceView();

        setLayout(new java.awt.BorderLayout());

        jComboBox1.setModel(jComboBox1.getModel());
        jComboBox1.setMinimumSize(new java.awt.Dimension(100, 20));
        jComboBox1.setPreferredSize(new java.awt.Dimension(100, 20));
        add(jComboBox1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

    public JComboBox getComboBox() {
        return jComboBox1;
    }
    
}

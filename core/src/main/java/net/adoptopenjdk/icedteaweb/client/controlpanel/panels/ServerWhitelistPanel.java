/* SecuritySettingsPanel.java -- Display possible security settings.
Copyright (C) 2010 Red Hat

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.adoptopenjdk.icedteaweb.client.controlpanel.panels;

import net.adoptopenjdk.icedteaweb.Assert;
import net.adoptopenjdk.icedteaweb.client.controlpanel.NamedBorderPanel;
import net.adoptopenjdk.icedteaweb.i18n.Translator;
import net.adoptopenjdk.icedteaweb.jdk89access.SunMiscLauncher;
import net.sourceforge.jnlp.config.DeploymentConfiguration;
import net.sourceforge.jnlp.util.UrlWhiteListUtils;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import static net.adoptopenjdk.icedteaweb.i18n.Translator.R;
import static net.sourceforge.jnlp.config.ConfigurationConstants.KEY_SECURITY_SERVER_WHITELIST;

/**
 * This provides a way for the user to display the server white list defined in <code>deployment.properties</code>.
 */
@SuppressWarnings("serial")
public class ServerWhitelistPanel extends NamedBorderPanel {
    private static final String ERROR_MARKER = "Error:";

    /**
     * This creates a new instance of the server white list panel.
     *
     * @param config Loaded DeploymentConfiguration file.
     */
    public ServerWhitelistPanel(final DeploymentConfiguration config) {
        super(Translator.R("CPServerWhitelist"), new BorderLayout());

        Assert.requireNonNull(config, "config");

        final List<String> whitelist = config.getPropertyAsList(KEY_SECURITY_SERVER_WHITELIST);
        final List<UrlWhiteListUtils.ValidatedWhiteListEntry> validatedWhitelist = UrlWhiteListUtils.getValidatedWhiteList();

        final JTable table = new JTable(createTableModel(whitelist, validatedWhitelist));
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoCreateRowSorter(true);
        TableColumnModel colModel=table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100);
        colModel.getColumn(1).setPreferredWidth(250);
        final ImageIcon icon = SunMiscLauncher.getSecureImageIcon("net/sourceforge/jnlp/resources/warn16.png");
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(/*"<html><body>" + */ (String)value /* + "</body></html>"*/);
                label.setOpaque(true);
                label.setBackground(table.getBackground());
                if (((String)value).startsWith(R("SWPVALIDATEWLURL")))  {
                    label.setIcon(icon);
                }
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                }
                label.setIconTextGap(5);
                //label.setHorizontalTextPosition(SwingConstants.CENTER);
                label.setVerticalTextPosition(SwingConstants.CENTER);
                return label;
            }
        });

        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    private TableModel createTableModel(final List<String> whitelist, final List<UrlWhiteListUtils.ValidatedWhiteListEntry> validatedWhitelist) {
        final String[] colNames = {R("SWPCol0Header"), R("SWPCol1Header")};
        return new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return whitelist.size();
            }

            public String getColumnName(int col) {
                return colNames[col].toString();
            }

            @Override
            public int getColumnCount() {
                return colNames.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0 : return whitelist.get(rowIndex);
                    case 1 : return (validatedWhitelist.get(rowIndex).getErrorMessage().isEmpty() ? validatedWhitelist.get(rowIndex).getWhiteListEntry() : /*ERROR_MARKER +  "!" +*/ validatedWhitelist.get(rowIndex).getErrorMessage());
                    default: throw new IllegalArgumentException();
                }
            }
        };
    }
}

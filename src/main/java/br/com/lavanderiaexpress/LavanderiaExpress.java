/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package br.com.lavanderiaexpress;

import br.com.lavanderiaexpress.view.TelaLogin;
import javax.swing.UIManager;

/**
 *
 * @author Gusta
 */
public class LavanderiaExpress {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Na falha, segue com o visual padrão
        }
        
        java.awt.EventQueue.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
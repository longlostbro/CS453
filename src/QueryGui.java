import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by longl on 5/24/2016.
 */
public class QueryGui extends JFrame {
    private final SuggestionGenerator generator;
    JTextArea resultsTextArea;
    JTextField queryTextField;
    public QueryGui(SuggestionGenerator generator)
    {
        super("Query Generator");
        this.generator = generator;
        //setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        queryTextField = new JTextField();
        queryTextField.setFocusTraversalKeysEnabled(false);
        queryTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    //JOptionPane.showMessageDialog(null, e.getKeyCode());
                    resultsTextArea.setText(generator.generateSuggestions(queryTextField.getText()));
                }
            }
        });
        resultsTextArea = new JTextArea();
        mainPanel.add(queryTextField,BorderLayout.NORTH);
        mainPanel.add(resultsTextArea,BorderLayout.CENTER);
        this.setContentPane(mainPanel);
        setSize(600,400);
        setVisible(true);
    }


}

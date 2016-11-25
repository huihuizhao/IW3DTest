package interpolation;
import java.awt.*;
import javax.swing.*;

public class InfoDialog extends JFrame
{
  TextArea textarea;
  protected Button button;
  public InfoDialog(Frame parent, String title, String message)
  {
    super(title);
    this.getContentPane().setLayout(new BorderLayout(15, 15));
    textarea = new TextArea(message, 15, 80);
    textarea.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
    textarea.setEditable(false);
    this.getContentPane().add(textarea);
    this.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int xPos, yPos;
    xPos = (int)(screenSize.getWidth() / 2.0) - this.getWidth() / 2;
    yPos = (int)(screenSize.getHeight() / 2.-0) - this.getHeight() / 2;
    this.setLocation(xPos, yPos);
  }
}

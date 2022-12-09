import javax.swing.*;  

public class ProgressBarTest extends JFrame
{
  JProgressBar progress;  
  
  ProgressBarTest()
  {
    // Create the progressBar
    progress = new JProgressBar(0,1000);
    // Set the position of the progressBar
    progress.setBounds(35,40,165,30);
    // Initialize the progressBar to 0   
    progress.setValue(0);  
    // Show the progress string
    progress.setStringPainted(true);  
    // Add the progressBar to the frame
    add(progress);  
    setSize(250,150);  
    setLayout(null);  
  }  
  
  // function to increase the progressBar
  public void loop()
  {
    int i=0; 
    while(i <= 1000)
    {
      // fills the bar
      progress.setValue(i);  
      i = i + 10;  
      try
      {
        // delay the thread 
        Thread.sleep(120);
      }
      catch(Exception e){}
    }
  }
  
  public static void main(String[] args) 
  {  
    ProgressBarTest frame = new ProgressBarTest();  
    frame.setVisible(true);  
    frame.loop();  
  }  
}
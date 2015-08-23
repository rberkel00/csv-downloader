import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class CSVDownloader extends JFrame {

	JLabel label;
	JLabel label2;
	JTextField tf;
	JTextField tf2;
	JLabel label3;
	JButton button;
	JLabel label4;
	String dir;
	String u;
	boolean error;
	
	public CSVDownloader() {
		setLayout(new FlowLayout());
		label = new JLabel("Enter URL: ");
		add(label);
		label2 = new JLabel("Enter Path: ");
		tf = new JTextField(50);
		add(tf);
		add(label2);
		tf2 = new JTextField(50);
		add(tf2);
		button = new JButton("Download");
		add(button);
		label3 = new JLabel("");
		label4 = new JLabel("");
		add(label3);
		add(label4);
		event e = new event();
		button.addActionListener(e);
		error = false;
	}
	
	public String getDir() {
		return dir;
	}
	
	public String getU() {
		return u;
	}
	
	public class event implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				error = false;
				u = tf.getText();
				dir = tf2.getText();
				
				URL url;
			    InputStream is = null;
			    BufferedReader br;
			    String line;
			    
			    int groupCount = 0;
			    int count = 0;
			    File[] files = new File[1000];
			    String[] fileNames = new String[1000];
			    boolean regular = true;
			    BufferedWriter[] bw = new BufferedWriter[50];
			    FileOutputStream[] fos = new FileOutputStream[50];
			    
			    try {
			    	url = new URL(getU());
			        is = url.openStream();  // throws an IOException
			        br = new BufferedReader(new InputStreamReader(is));
			        
			        //Find all of the csv files and put it in String[] array
			        //Find names of the csv files and put it in String[] names
			        while ((line = br.readLine()) != null) {
			            regular = true;
			        	if(line.contains(".csv")) {
			                //System.out.println(line.trim() + "\n" + count);
			            	//find link substring
			                int firstQ = line.indexOf("\"");
			                int lastQ = line.indexOf("\"", firstQ + 1);
			                //put in correct sub array
			                int underscore = line.lastIndexOf("_");
			                if (underscore == -1) regular = false;
			                int period = line.lastIndexOf('.');
			                boolean exists = false;
			                if (regular) {
			                	for (int x = 0; x < groupCount && !exists; x++) {
			                		if (fileNames[x].equals(line.substring(underscore + 1, period))) {
			                			String temp = "http://cfe.cboe.com" + line.substring(firstQ + 1, lastQ);
			                			URL website = new URL(temp);
			                			InputStream in = website.openStream();
			                			BufferedReader buff = new BufferedReader(new InputStreamReader(in));
			                			String l;
			                			buff.readLine();
			                			buff.readLine();
			                			while ((l = buff.readLine()) != null) {
			                				bw[x].write(l);
			                				bw[x].newLine();
			                			}
			                			count++;
			                			buff.close();
			                			in.close();
			                			exists = true;
			                		}
			                	}
			                	if (!exists) {
			                		files[groupCount] = new File(dir, line.substring(underscore + 1, period) + ".csv");
			                		fileNames[groupCount] = line.substring(underscore + 1, period);
			                		String temp = "http://cfe.cboe.com" + line.substring(firstQ + 1, lastQ);
			                		URL website = new URL(temp);
			                		InputStream in = website.openStream();
			                		BufferedReader buff = new BufferedReader(new InputStreamReader(in));
			                		fos[groupCount] = new FileOutputStream(files[groupCount]);
			                		bw[groupCount] = new BufferedWriter(new OutputStreamWriter(fos[groupCount]));
			                		String l;
			                		while ((l = buff.readLine()) != null){
			                			bw[groupCount].write(l);
			                			bw[groupCount].newLine();
			                		}
			                		groupCount++;
			                		count++;
			                		buff.close();
			                		in.close();
			                	}
			                }
			            }
			        }
			    } catch (MalformedURLException mue) {
			         label3.setText("Input Error");
			         error = true;
			    } catch (SocketException soc) {
			    	label3.setText("Internet Connection Error");
			    	error = true;
				} catch (IOException ioe) {
					label3.setText("Input Error");
					error = true;
			    } finally {
			        try {
			            if (is != null) is.close();
			        } catch (IOException ioe) {
			        }
			    }
			    if (!error) {
			    	label3.setText("Download Complete ->");
			    	label4.setText("Found " + count + " files, combined into " + groupCount + " files");
			    }
			    for (int x = 0; x < groupCount; x++) {
			    	bw[x].close();
			    	fos[x].close();
			    }
			} catch (Exception ex) {}
		}
	}
	
	
	public static void main(String[] args) {
	    CSVDownloader gui = new CSVDownloader();
	    gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    gui.setSize(700, 150);
	    gui.setTitle("CSVDownloader");
	    gui.setVisible(true);
	}

}

package burp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class BurpExtender implements IBurpExtender, IContextMenuFactory
{
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    
	public static void main(String [] args)
	{
		
	}
	
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        callbacks.setExtensionName("Custom Site Exporter");
        callbacks.registerContextMenuFactory(BurpExtender.this);
    }

    public void writeStringToFile(String Output, File file){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file));
            out.write(Output);
            JOptionPane.showMessageDialog(null, "File saved successfully.");
        } catch ( IOException e1 ) {
        	JOptionPane.showMessageDialog(null, "Error saving file: " + e1.getMessage());
        } finally {
        	try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    public ArrayList<String> extensionTreatment(boolean isPng, boolean isJpg, boolean isGif, boolean isJs, boolean isCss, boolean isWoff, boolean isWoff2, boolean isIco, boolean isJson, boolean isPdf){
		ArrayList<String> extensionList = new ArrayList<String>();
		if(!isPng){
			extensionList.add("png");
		}
		if(!isJpg){
			extensionList.add("jpg");
			extensionList.add("jpeg");
		}
		if(!isGif){
			extensionList.add("gif");
		}
		if(!isJs){
			extensionList.add("js");
		}
		if(!isCss){
			extensionList.add("css");
		}
		if(!isWoff){
			extensionList.add("woff");
		}
		if(!isWoff2){
			extensionList.add("woff2");
		}
		if(!isIco){
			extensionList.add("ico");
		}
		if(!isJson){
			extensionList.add("json");
		}
		if(!isPdf){
			extensionList.add("pdf");
		}
		return extensionList;
	}
    
    public String createOutputForAppScanStandard(IHttpRequestResponse tmp[],ArrayList<String> extensionList){
    	callbacks.printOutput("OK, we called CreateOutput: " + tmp.length);
    	String Output = "";
    	String NewLine = "";
    	for(int i = 0; i < tmp.length; i++){
        	String protocol =  tmp[i].getHttpService().getProtocol();
    		String method = helpers.analyzeRequest(tmp[i].getRequest()).getMethod();
    		String path = "/";
    		String tmpStr = new String(tmp[i].getRequest());
    		int firstslash = tmpStr.indexOf(" ");
    		int secondslash = tmpStr.indexOf(" ", firstslash + 1);
    		int questionmark = tmpStr.indexOf("?", firstslash + 1);
    		if(questionmark < secondslash && questionmark > 0){
    			secondslash = questionmark;
    		}
    		path = tmpStr.substring(firstslash + 1, secondslash).replace("\"", "%22");
    		int port = tmp[i].getHttpService().getPort();
    		String host =  tmp[i].getHttpService().getHost();
    		NewLine = "" + protocol + "://" + host + path;
				if(!(containsIgnoreCase(Output,NewLine))){
					if (!containsExtensionIgnoreCase(NewLine,extensionList)){
						Output += NewLine+ "\n";
					}
				}
    	}
    	return Output;
    }
    
    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }
    
    public static boolean containsExtensionIgnoreCase(String str, ArrayList<String> extensionList) {
		String extension = str.substring(str.lastIndexOf('.') + 1);
        return extensionList.contains(extension.toLowerCase());
    }
    
    @Override
    public ArrayList<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        ArrayList<JMenuItem> menu = new ArrayList<JMenuItem>();
        
        byte ctx = invocation.getInvocationContext();
        
    	JMenuItem main = new JMenuItem("Custom Site Exporter", null);
        FileFilter filter = new FileNameExtensionFilter("TXT File","txt");
                
        JFileChooser fileChooser = new JFileChooser(){
        	@Override
            public void approveSelection(){
                File f = getSelectedFile();
                if(f.exists() && getDialogType() == SAVE_DIALOG){
                    int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }        
        };
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setSelectedFile(new File("burpSiteMapExport.txt"));
    	
    	main.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				
				
				
				JFrame frame = new JFrame("Keeping extensions");
				frame.setLocationRelativeTo(null);
				JPanel mainPanel = new JPanel();
				mainPanel.setPreferredSize(new Dimension(640, 110));
				mainPanel.setLayout(new BorderLayout());
				JPanel checkBoxPanel = new JPanel();
				
				JLabel jlabel = new JLabel("Which extention keeping ?");
				JCheckBox extention1 = new JCheckBox("png");
				extention1.setSelected(true);
				JCheckBox extention2 = new JCheckBox("jpg");
				extention2.setSelected(true);
				JCheckBox extention3 = new JCheckBox("gif");
				extention3.setSelected(true);
				JCheckBox extention4 = new JCheckBox("js");
				extention4.setSelected(true);
				JCheckBox extention5 = new JCheckBox("css");
				extention5.setSelected(true);
				JCheckBox extention6 = new JCheckBox("woff");
				extention6.setSelected(true);
				JCheckBox extention7 = new JCheckBox("woff2");
				extention7.setSelected(true);
				JCheckBox extention8 = new JCheckBox("ico");
				extention8.setSelected(true);
				JCheckBox extention9 = new JCheckBox("json");
				extention9.setSelected(true);
				JCheckBox extention10 = new JCheckBox("pdf");
				extention10.setSelected(true);
				JButton submitButton = new JButton("Export");
				
				checkBoxPanel.add(jlabel, BorderLayout.NORTH);
				checkBoxPanel.add(extention1);
				checkBoxPanel.add(extention2);
				checkBoxPanel.add(extention3);
				checkBoxPanel.add(extention4);
				checkBoxPanel.add(extention5);
				checkBoxPanel.add(extention6);
				checkBoxPanel.add(extention7);
				checkBoxPanel.add(extention8);
				checkBoxPanel.add(extention9);
				checkBoxPanel.add(extention10);
				checkBoxPanel.add(submitButton,BorderLayout.SOUTH);

				mainPanel.add(checkBoxPanel);

				frame.add(mainPanel);

				frame.pack();
				frame.setVisible(true);
				
				submitButton.addActionListener(new ActionListener()
				{
				  public void actionPerformed(ActionEvent e)
				  {
					boolean pngIsSelected = extention1.isSelected();
					boolean jpgIsSelected = extention2.isSelected();
					boolean gifIsSelected = extention3.isSelected();
					boolean jsIsSelected = extention4.isSelected();
					boolean cssIsSelected = extention5.isSelected();
					boolean woffIsSelected = extention6.isSelected();
					boolean woff2IsSelected = extention7.isSelected();
					boolean icoIsSelected = extention8.isSelected();
					boolean jsonIsSelected = extention9.isSelected();
					boolean pdfIsSelected = extention10.isSelected();
					
					frame.dispose();
					
					IHttpRequestResponse tmp2  = invocation.getSelectedMessages()[0];
					IHttpRequestResponse[] tmp = callbacks.getSiteMap(tmp2.getHttpService().getProtocol() + "://" + tmp2.getHttpService().getHost());
					if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
						File outputFile = fileChooser.getSelectedFile();
						writeStringToFile(createOutputForAppScanStandard(tmp,extensionTreatment(pngIsSelected,jpgIsSelected,gifIsSelected,jsIsSelected,cssIsSelected,woffIsSelected,woff2IsSelected,icoIsSelected,jsonIsSelected,pdfIsSelected)), outputFile);
					}
				  }
				});
            }
        });
    	
    	menu.add(main);
        
        return menu;
    }
}

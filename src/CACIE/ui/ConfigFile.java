package CACIE.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ConfigFile {
	public static ArrayList<ArrayList<String>> readParametersFromFile(
			String fileName) {
		ArrayList<ArrayList<String>> returnArray = new ArrayList<ArrayList<String>>();
		BufferedReader in;
		ArrayList<String> OperatorArray = new ArrayList<String>();
		ArrayList<String> ConfigArray = new ArrayList<String>();
		try {
			in = new BufferedReader(new FileReader(fileName));

			//FunctionListを読む
			String st = in.readLine();
			StringTokenizer stkn = new StringTokenizer(st);
			if(!stkn.nextToken().equals("FunctionList"))
				System.out.println("ConfigFile : This is not FunctionList");	
		
			while(true){
				if(!stkn.hasMoreTokens()){
					String commingNewLine;
					while(true){
						//改行飛ばし
						commingNewLine = in.readLine();
						if(!commingNewLine.equals(""))
							break;
					}
					stkn = new StringTokenizer(commingNewLine);
				}
					
				String s = stkn.nextToken();
				if(s.equals("\\e")){
					break;
				}
				else
					OperatorArray.add(s);
			}
						
			//ConfigListを読む
			if(!stkn.hasMoreTokens()){
				while(true){
					//改行飛ばし
					st = in.readLine();
					if(!st.equals(""))
						break;
				}
				stkn = new StringTokenizer(st);
			}
			String allConfigLines = "";
			if (stkn.nextToken().equals("ConfigList")) {
				if(stkn.hasMoreTokens()){
					//ConfigListの後にも行が続いていた場合
					allConfigLines = st.substring(10, st.length()) + " ";
				}
				
				while ((st = in.readLine()) != null) {
					// まず全部の行をくっつける
					allConfigLines = allConfigLines.toString() + " "
							+ st.toString();
				}
				in.close();
			} else if (st.equals("\\e")) {
				System.out.println("There are No Config");
			} else {
				System.out.println("Illegal Config File");
			}
			// 各ConfigLineに分解していく
			// 最後に残ったのが\e一つだけなら成功
			StringTokenizer tokenizer = new StringTokenizer(allConfigLines);
			boolean popFlag = false;
			System.err.println("Configs for generation are...:");
			while (tokenizer.hasMoreTokens()) {
				String aWord = tokenizer.nextToken();
				if (aWord.equals("\\e")) {
					if (ConfigArray.size() > 0) {
						System.err.println
							("There are " + ConfigArray.size() + " configs.");
						break;
					} else {
						System.err
								.println("No Config Line. Use Default Configs.");
						break;
					}
				} else {
					String configLine = "";
					if (aWord.equals("CONFIG:")) {
						popFlag = true;
						configLine = new String("");
						while (popFlag) {
							String value = tokenizer.nextToken();
							if (value.equals("\\e")) {
								popFlag = false;
							} else
								configLine = configLine + " "
										+ value.toString();
						}
						System.err.println(configLine);
						ConfigArray.add(configLine);
					}
				}
			}

		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		returnArray.ensureCapacity(2);
		returnArray.add(OperatorArray);
		returnArray.add(ConfigArray);
		return returnArray;
	}
	
    public static ArrayList<ArrayList<String>> readParametersFromFile(
        InputStream input) {
    ArrayList<ArrayList<String>> returnArray = new ArrayList<ArrayList<String>>();
    BufferedReader in;
    ArrayList<String> OperatorArray = new ArrayList<String>();
    ArrayList<String> ConfigArray = new ArrayList<String>();
    try {
        in = new BufferedReader(new InputStreamReader(input));

        //FunctionListを読む
        String st = in.readLine();
        StringTokenizer stkn = new StringTokenizer(st);
        if(!stkn.nextToken().equals("FunctionList"))
            System.out.println("ConfigFile : This is not FunctionList");    
    
        while(true){
            if(!stkn.hasMoreTokens()){
                String commingNewLine;
                while(true){
                    //改行飛ばし
                    commingNewLine = in.readLine();
                    if(!commingNewLine.equals(""))
                        break;
                }
                stkn = new StringTokenizer(commingNewLine);
            }
                
            String s = stkn.nextToken();
            if(s.equals("\\e")){
                break;
            }
            else
                OperatorArray.add(s);
        }
                    
        //ConfigListを読む
        if(!stkn.hasMoreTokens()){
            while(true){
                //改行飛ばし
                st = in.readLine();
                if(!st.equals(""))
                    break;
            }
            stkn = new StringTokenizer(st);
        }
        String allConfigLines = "";
        if (stkn.nextToken().equals("ConfigList")) {
            if(stkn.hasMoreTokens()){
                //ConfigListの後にも行が続いていた場合
                allConfigLines = st.substring(10, st.length()) + " ";
            }
            
            while ((st = in.readLine()) != null) {
                // まず全部の行をくっつける
                allConfigLines = allConfigLines.toString() + " "
                        + st.toString();
            }
            in.close();
        } else if (st.equals("\\e")) {
            System.out.println("There are No Config");
        } else {
            System.out.println("Illegal Config File");
        }
        // 各ConfigLineに分解していく
        // 最後に残ったのが\e一つだけなら成功
        StringTokenizer tokenizer = new StringTokenizer(allConfigLines);
        boolean popFlag = false;
        System.err.println("Configs for generation are...:");
        while (tokenizer.hasMoreTokens()) {
            String aWord = tokenizer.nextToken();
            if (aWord.equals("\\e")) {
                if (ConfigArray.size() > 0) {
                    System.err.println
                        ("There are " + ConfigArray.size() + " configs.");
                    break;
                } else {
                    System.err
                            .println("No Config Line. Use Default Configs.");
                    break;
                }
            } else {
                String configLine = "";
                if (aWord.equals("CONFIG:")) {
                    popFlag = true;
                    configLine = new String("");
                    while (popFlag) {
                        String value = tokenizer.nextToken();
                        if (value.equals("\\e")) {
                            popFlag = false;
                        } else
                            configLine = configLine + " "
                                    + value.toString();
                    }
                    System.err.println(configLine);
                    ConfigArray.add(configLine);
                }
            }
        }

    } catch (IOException e) {
        System.err.println(e);
        System.exit(1);
    }
    returnArray.ensureCapacity(2);
    returnArray.add(OperatorArray);
    returnArray.add(ConfigArray);
    return returnArray;
}
}
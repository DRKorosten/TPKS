package support;

import support.Minimization.BoolFunctionsText;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;

//B (y1) (y2) x1^2^4 (y3) E

public class Main
{
    static JFrame frame = new JFrame(), jf;
    public static JTextField pole = new JTextField();
    static ArrayList<String> all;
    static ArrayList<String> allSignals;
    static Integer[][] Vertex; // 
    static Integer[][] VertexSignals; // 
	static int countSignals;

	static CODE_MURA code;
	
	// //
	static mxGraph grap;
    static mxGraphComponent graphComponent;
    static Integer[][] Matrix;
    static String[] LSA;
    static ArrayList<ArrayList<String>> MuraMatrix;
    static String Vertexes = "";
    static JMenuItem load;
    static JMenuItem save;
    static ArrayList<Integer> marked;
	public static JButton jb5,jb6,jb7, jb8, jb9;
    Main aThis = this;

    static Table code2;

    
    public Main() {
    	aThis = this;
    }

    public static void main(String args[])
    {
    	new Main();

    	all = new ArrayList<String>();
    	allSignals = new ArrayList<String>();
    	countSignals = 0;
    	// 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        //    
        contentPane.setLayout(new BorderLayout());
        
        JPanel jp2 = new JPanel();
        JPanel jp3 = new JPanel();
        jp2.setLayout(new BorderLayout());
        jb5 = new JButton("Validate");
        jb6 = new JButton("Graph"); //B (y1,y2) (y3) x1,x2^2^4 E
        jb7 = new JButton("Coding Nodes");
        jb7.setEnabled(false);
        jb8 = new JButton("JK Trigers");
        jb8.setEnabled(false);
        jb9 = new JButton("Elem base + VHDL");
        jb9.setEnabled(false);

        jp3.add(jb5);
        jp3.add(jb6);jp3.add(jb7); jp3.add(jb8); jp3.add(jb9);
        jp2.add(jp3, BorderLayout.NORTH);
        contentPane.add(jp2,BorderLayout.CENTER);
        
        frame.setSize(800,70);
        frame.setLocation(300, 300);
        frame.setVisible(true);
        
      // 

        
      //      .
        jb5.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
            	countSignals = 0;
            	Vertex = null; VertexSignals = null;
            	all = new ArrayList<String>();
            	allSignals = new ArrayList<String>();
            	//  ,    !
            	if (!pole.getText().isEmpty()) {
            		checkOut(); //   

            		checkVertexes();

            	} else {
            		showErrorMessage("   ");
            	}
            }
        });
        
        //  .
        jb6.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
            	Matrix = Vertex;
            	marked = new ArrayList<Integer>();
            	
                StringTokenizer st = new StringTokenizer(pole.getText(), " ");
                LSA = new String[st.countTokens()];
                for (int i = 0; i < LSA.length; i++) {
                    LSA[i] = st.nextToken();
                }

                MuraMatrix = new ArrayList<ArrayList<String>>();
                
                addVertex(); //  ( )
                Vertexes = "0";
                marked.add(0);
                int place = 0;
                for (int i = 0; i < Matrix.length; i++) {
                    if (Matrix[place][i] != null) {
                        try {
							make(i, "-", 0);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} // , ,  .
                        break;
                    }
                }
                
//                System.out.println(Vertexes);
                jf = new JFrame("");
                jf.setVisible(true);
                jf.setSize(500, 500);
				jf.setLocationRelativeTo(null);

                // 
                JMenuBar mbar = new JMenuBar();
                JMenu file = new JMenu("FILE");
                file.add(save = new JMenuItem("Save"));
                file.add(load = new JMenuItem("Load"));
                mbar.add(file);
                addMenuLoadAndSave();

                //    
                Container contentPane = jf.getContentPane();
                contentPane.setLayout(new BorderLayout());
                contentPane.add(mbar,BorderLayout.NORTH);

                formThisWay();
                jb7.setEnabled(true);
            }
        });
        
      //  .
        jb7.addActionListener( new ActionListener()
        {
            private Main aThis;

			public void actionPerformed(ActionEvent e) {
            	code = new CODE_MURA(MuraMatrix, Vertexes, aThis);
                code.setSize(500,500);
				code.setLocationRelativeTo(null);
                code.setVisible(true);
                code.start();
                code.ppaint();
                
                jb8.setEnabled(true);
            }
        });
        
        jb8.addActionListener( new ActionListener()
        {
			public void actionPerformed(ActionEvent e) {
        		code2 = new Table("TABLE", 
            			code.getAutomat(),
            			code.getCodeAutomat(), allSignals, 
            			code.getVertexAutomat());
				code2.setSize(800,400);
				code2.setLocationRelativeTo(null);
        		jb9.setEnabled(true);
            }
        });
        
        jb9.addActionListener( new ActionListener()
        {
			public void actionPerformed(ActionEvent e) {
//				System.out.println();
				Object[][] table = code2.getTable();
		        String[] header = code2.getHeader();

		        int XCount = 0;
		        int YCount = 0;

		        for (int i = 0; i < header.length; i++) {
		            if (header[i].contains("x")) XCount++;
		            if (header[i].contains("y")) YCount++;
		        }

		        int Count_Length = code2.getCOUNT();

		        BoolFunctionsText ad =
		            new BoolFunctionsText("FUNCTIONS", table, header,
		        	XCount, YCount, Count_Length);
				ad.setSize(800,800);
				ad.setLocationRelativeTo(null);
            }
        });
    }    
    
    private static void checkVertexes() {
    	boolean visjaw = false;
    	boolean nedost = false;
    	String s = "";
    	//  
    	s += " \n";
		for (int i = 0; i < Vertex.length - 1; i++) {
			boolean find1 = true;
			for (int j = 0; j < Vertex.length; j++) {
				if (Vertex[i][j] != null) {
					find1 = false;
				}
			}
			if (find1) {
				visjaw = true;
				s += " -   " + i + "\n";
			}
		}
		//  
    	s += " \n";
		for (int i = 1; i < Vertex.length; i++) {
			boolean find2 = true;
			for (int j = 0; j < Vertex.length; j++) {
				if (Vertex[j][i] != null) {
					find2 = false;
				}
			}
			if (find2) {
				nedost = true;
				s += " -   " + i + "\n";
			}
		}

		if (!nedost & !visjaw) {
			JOptionPane.showMessageDialog(null, "OK!",
					"OK!", JOptionPane.INFORMATION_MESSAGE);
		} else {
			showErrorMessage(s);
		}
	}

//	private static void outMatrix1() {
//    	for (int i = 0; i < Vertex.length; i++) {
//    		for (int j = 0; j < Vertex.length; j++) {
//    			if (Vertex[i][j] == null) System.out.print("0 ");
//    			else System.out.print(Vertex[i][j] + " ");
//        	}
//    		System.out.println();
//    	}
//    }
//
//    private static void outMatrix2() {
//    	if (VertexSignals != null) {
//    		for (int i = 0; i < VertexSignals.length; i++) {
//        		for (int j = 0; j < VertexSignals[i].length; j++) {
//        			if (VertexSignals[i][j] == null) System.out.print("0 ");
//        			else System.out.print(VertexSignals[i][j] + " ");
//            	}
//        		System.out.println();
//        	}
//    	}
//    }

    //  
	private static void checkOut() {
		ArrayList<String> vertexBlock = new ArrayList<String>();
		
		// 
		String LSA = pole.getText();
		//
		StringTokenizer tokens = new StringTokenizer(LSA," ");
		//  
		int count = tokens.countTokens();
		//  
		Vertex = new Integer[count][count];
		
		//  
		String firstBlock = tokens.nextToken();
		//  , 
		boolean endExist = true;
		
		if (!LSA.endsWith("E")) {
			showErrorMessage(" ");
			endExist = false;
		}
		
		if (firstBlock.contentEquals("B") & endExist) {
			Vertex[0][1] = 1; //     .
			//  
			for (int i = 1; i < count; i++) {
				String nextBlock = tokens.nextToken();
				//   ...
				if (nextBlock.contains("(")) {
					//    !
					if (nextBlock.contains("^")) {
						StringTokenizer allow = new StringTokenizer(nextBlock, "^");
						//  ,     ...
						int ccc = allow.countTokens();
						if ((ccc > 2) | (ccc == 1)) {
							showErrorMessage("  ");
						} else {
							//  
							String block = allow.nextToken();
							//     
							int jump = -1;
							try {
								jump = Integer.parseInt(allow.nextToken());
							} catch (NumberFormatException e) {}
							//     ^1s  ^lol  ^^
							if (jump == -1) {
								// ,     .
								showErrorMessage("  ");
							//      ^656
							} else if (jump > count) {
								//   .
								showErrorMessage("   ");
							} else if (jump == i) {
								//   .
								showErrorMessage("     ");
							} else if (jump == 0) {
								//   .
								showErrorMessage("   ,      ");
							} else { //     !
								//     ,  
								if (!checkBlock(block,'y','Y', i)) {
									showErrorMessage("  ,  Y");
								} else {
									Vertex[i][jump] = 1;
									vertexBlock.add(nextBlock);
								}
							}
						}
					//     
					} else {
						if (!checkBlock(nextBlock,'y','Y', i)) {
							showErrorMessage("  ,  Y");
						} else {
							Vertex[i][i+1] = 1;
							vertexBlock.add(nextBlock);
						}
					}
				} else if (!nextBlock.contentEquals("E")) { //    (    )
					//     
					if (!nextBlock.contains("^")) {
						showErrorMessage("  ");
					} else {
						StringTokenizer allow = new StringTokenizer(nextBlock, "^");
						//  ,     ...
						int ccc = allow.countTokens();
						//     (3 -     )
						if (ccc != 3) {
							showErrorMessage("  ");
						} else {
							//  
							String block = allow.nextToken();
							//     
							int jumpYES = -1, jumpNO = -1;
							try {
								jumpYES = Integer.parseInt(allow.nextToken());
								jumpNO = Integer.parseInt(allow.nextToken());
							} catch (NumberFormatException e) {}
							//     ^1s  ^lol  ^^
							if ((jumpYES == -1) | (jumpNO == -1)) {
								// ,     .
								showErrorMessage("  ");
							//      ^656
							} else if ((jumpYES > count) | (jumpNO == count)) {
								//   .
								showErrorMessage("   ");
							} else if ((jumpYES == i) | (jumpNO == i)) {
								//   .
								showErrorMessage("     ");
							} else if ((jumpYES == 0) | (jumpNO == 0)) {
								//   .
								showErrorMessage("   ,      ");
							} else { //     !
								//     ,  
								if (!checkBlock(block,'x','X', i)) {
									showErrorMessage("  ,  ");
								} else {
									Vertex[i][jumpYES] = 2;
									Vertex[i][jumpNO] = -2;
									vertexBlock.add(nextBlock);
								}
							}							
						}
					}
				}
			}
		} else if (endExist) {
			showErrorMessage(" ");
		}

		//  
		VertexSignals = new Integer[count][countSignals];
		
		for (int i = 0; i < all.size(); i++) {
			StringTokenizer ggg = new StringTokenizer(all.get(i),",");
			ggg.nextToken();
			int place = Integer.parseInt(ggg.nextToken());
			int place2 = Integer.parseInt(ggg.nextToken());
			VertexSignals[place2][place] = 1;
		}

		VertexSignals = null;




	}

	private static void showErrorMessage(String string) {
		JOptionPane.showMessageDialog(null, string, 
				"", JOptionPane.ERROR_MESSAGE);
	}

	// 
	private static boolean checkBlock(String nextBlock, char c, char d, int blockNumber) {
		//  ,  ! 
		if (nextBlock.charAt(0) == '(') 
			nextBlock = nextBlock.substring(1, nextBlock.length() -1);
		StringTokenizer st = new StringTokenizer(nextBlock,",");
		int length = st.countTokens();
		
		//  
		for (int i = 0; i < length; i++) {
			String s = st.nextToken();
			
			int place = allSignals.size();
			if (!allSignals.contains(s)) {
				countSignals++;
				allSignals.add(s);
			} else {
				place = allSignals.indexOf(s);
			}
			
			//    .
			all.add(s+","+place+","+blockNumber);
			
			//   ,  ,  
			if ((s.charAt(0) == c) | (s.charAt(0) == d)) {
				s = s.substring(1); //     !
				try { //     ,  ,  
					Integer.parseInt(s);
				// false .
				} catch (NumberFormatException e) { return false; }
			} else { //  ,    !
				return false;
			}
		}
		return true;
	}
	
	////////////////////////////////////////////////////////////////
	/////////////////////////    /////////
	////////////////////////////////////////////////////////////////
	
	//place -  
    //x -  
    //was -      
    private static void make(int place, String x, int was) throws InterruptedException {
        //1.   
        if (place == Matrix.length - 1) { //   .
            MuraMatrix.get(was).set(0, x);
        } else if (LSA[place].contains("x")) { //  ,  .
            if (x.contentEquals("-")) x = "";
                
            for (int i = 0; i < Matrix.length; i++) {
                if ((Matrix[place][i] != null) && (Matrix[place][i] == 2)) {
                    make(i, x + getX(LSA[place]), was);
                }
                if ((Matrix[place][i] != null) && (Matrix[place][i] == -2)) {
                    make(i, x + "!" + getX(LSA[place]), was);
                }
            }
        } else if (LSA[place].contains("y")) { //  y
            if (marked.contains(place)) { //    
                //ArrayList<String> ar = MuraMatrix.get(was); 
                MuraMatrix.get(was).set(marked.indexOf(place), x);
                //MuraMatrix.set(was, ar);
            } else {
                marked.add(place);
                addVertex(); //  
                MuraMatrix.get(was).set(MuraMatrix.size() - 1, x);
                Vertexes += " " + LSA[place] + "";

                int next_place = 0;
                for (int i = 0; i < Matrix.length; i++) {
                    if ((Matrix[place][i] != null) && (Matrix[place][i] == 1)) next_place = i;
                }
                make(next_place, "-", MuraMatrix.size() - 1);  
            }
        }
    }

    private static String getX(String string) {
		StringTokenizer st = new StringTokenizer(string, "^");
		return st.nextToken();
	}

	//    !
    private static void addVertex() {
        ArrayList<String> list = new ArrayList<String>();
        if (!MuraMatrix.isEmpty() & MuraMatrix.size() != 0) {
            for (int i = 0; i < MuraMatrix.size(); i++) {
                MuraMatrix.get(i).add("0");
                list.add("0");
            }
            list.add("0");
            MuraMatrix.add(list);
        } else {
            list.add("0");
            MuraMatrix.add(list);
        }

    }

    private static void formThisWay() {
        StringTokenizer st = new StringTokenizer(Vertexes, " ");
        LSA = new String[st.countTokens()];
        for (int i = 0; i < LSA.length; i++) {
            LSA[i] = st.nextToken();
        }

        // 
        grap = new mxGraph();
        Object parent = grap.getDefaultParent();

        mxStylesheet stylesheet = grap.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_OPACITY, 100);
        style.put(mxConstants.STYLE_FONTCOLOR, "#00000FF");
        stylesheet.putCellStyle("ROUNDED", style);

        grap.getModel().beginUpdate();
        try
        {
            //
            String settings = "ROUNDED;strokeColor=blue;fillColor=green";
            Object[] v = new Object[MuraMatrix.size()]; //  
            for (int i = 0; i < MuraMatrix.size(); i++) {
                    //   !
                    Point ptr = drawMiracle(i, MuraMatrix.size()); //  
                    v[i] = grap.insertVertex(parent, null,
                                    LSA[i]+"", ptr.x, ptr.y, 50,
                                    50, settings); //    !
            }
            for (int i = 0; i < MuraMatrix.size(); i++) {
                    for (int j = 0; j < MuraMatrix.size(); j++) {
                            if (!MuraMatrix.get(i).get(j).contains("0")) {
                                    //   
                                    //    !
                                    grap.insertEdge(parent, null,
                                                    MuraMatrix.get(i).get(j),
                                                    v[i], v[j]);
                            }
                    }
            }
        }
        finally
        {
            grap.getModel().endUpdate(); // ...
        }

        graphComponent = new mxGraphComponent(grap);
        jf.getContentPane().add(graphComponent);
    }

    private static Point drawMiracle(int i, int length) { // 
            //*************************************************
        //    
        // 1  = pi/180 
        //*************************************************
        double theta = ((i + 1) * 360/length) * (3.14/180);
        //*************************************************
        //    
        // x = r cos @
        // y = r sin @
        //*************************************************
        double x = 100 * Math.cos(theta);
        double y = 100 * Math.sin(theta);
        Point mark2 = new Point(120, 120);
        mark2.translate((int)x, (int)y);
        return mark2;
    }

    private static void addMenuLoadAndSave() {
        // 
        save.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                File FS;
                if (fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
                    FS = fc.getSelectedFile();
                    try {
                            FileWriter fw = new FileWriter(FS);
                            FS.delete(); //      
                            //  !   
                            FS.createNewFile();
                            String s = "";
                            for (int i = 0; i < MuraMatrix.size(); i++) {
                                    for (int j = 0; j < MuraMatrix.size(); j++) {
                                            //    ,
                                            //   
                                            s += MuraMatrix.get(i).get(j);
                                            //       
                                            if (j != MuraMatrix.get(i).size() - 1) { s += "_"; }
                                    }
                                    fw.write(s + "\r\n"); //       .
                                    s = ""; //    
                            }
                            fw.write(Vertexes); //  1
                            fw.close(); //  
                    } catch (Exception ex) {}
                }
            }
        });

        // 
        load.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                File FS;
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    FS = fc.getSelectedFile();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(FS));
                        String s; StringTokenizer st;
                        s = br.readLine();
                        //     
                        st = new StringTokenizer(s, "_");
                        //   ==  ,  
                        int leng = st.countTokens();
                        //  
                        MuraMatrix = new ArrayList<ArrayList<String>>();
                        for (int i1 = 0; i1 < leng; i1++) {
                            ArrayList<String> arr = new ArrayList<String>();
                            for (int i2 = 0; i2 < leng; i2++) {
                                arr.add("0");
                            }
                            MuraMatrix.add(arr);
                        }
                        for (int j = 0; j < leng; j++) {
                            MuraMatrix.get(0).set(j, st.nextToken()); //   
                        }
                        for (int i = 1; i < leng; i++) {
                            s = br.readLine();
                            st = new StringTokenizer(s, "_");
                            for (int j = 0; j < leng; j++) {
                                MuraMatrix.get(i).set(j, st.nextToken()); //   
                            }
                        }
                        Vertexes = br.readLine();
                        br.close();
                        jf.getContentPane().remove(graphComponent);
                        formThisWay(); //   ,   
                        grap.refresh();
                        jf.repaint();
                    } catch (Exception ex) {}
                }
            }
        });
    }

	public ArrayList<ArrayList<String>> getMura() {
		return MuraMatrix;
	}

	public String getVertexes() {
		return Vertexes;
	}
}
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
    static JFrame frame = new JFrame("LSA"), jf; //основное окно программы
    public static JTextField pole = new JTextField();
    static ArrayList<String> all;
    static ArrayList<String> allSignals;
    static Integer[][] Vertex; //матрица связности
    static Integer[][] VertexSignals; //матрица сигналов
	static int countSignals;

	static CODE_MURA code;
	
	//АВТОМАТ МУРА//
	static mxGraph grap;
    static mxGraphComponent graphComponent;
    static Integer[][] Matrix;
    static String[] LSA;
    static ArrayList<ArrayList<String>> MuraMatrix;
    static String Vertexes = "";
    static JMenuItem load;
    static JMenuItem save;
    static ArrayList<Integer> marked;
	static JButton jb7, jb8, jb9;
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
    	//создание окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        //расположение на более верхнем уровне
        contentPane.setLayout(new BorderLayout());
        
        JPanel jp1 = new JPanel();
        pole.setPreferredSize(new Dimension(500, 22));
        jp1.add(pole);
        
        //создание поля и менюшек
        
        JPanel jp2 = new JPanel();
        JPanel jp3 = new JPanel();
        jp2.setLayout(new BorderLayout());
        JButton jb1 = new JButton("Check!");
        JButton jb2 = new JButton("Save");
        JButton jb3 = new JButton("Load");
        JButton jb4 = new JButton("Clear");
        JButton jb5 = new JButton("CheckVertex");
        JButton jb6 = new JButton("Make Graph"); //B (y1,y2) (y3) x1,x2^2^4 E
        jb7 = new JButton("Code Graph");
        jb7.setEnabled(false);
        jb8 = new JButton("Show Table");
        jb8.setEnabled(false);
        jb9 = new JButton("VHDL (Minimize)");
        jb9.setEnabled(false);
        
        jp3.add(jb1); jp3.add(jb2); jp3.add(jb3); jp3.add(jb4);
        jp3.add(jb5);
        jp3.add(jb6);jp3.add(jb7); jp3.add(jb8); jp3.add(jb9);
        jp2.add(jp3, BorderLayout.NORTH);
        String s = "Правила: B - начало, (y1,y2) - операции, " +
        		"х1,х2^1^2 - условие с прыжками ";
        String s2 = "на 1 и 2 блок по ДА, НЕТ, " +
        		"(y1)^1 - безусловный прыжок" +
        		"," +
        		" E - конец";
        jp2.add(new JLabel(s), BorderLayout.CENTER);
        jp2.add(new JLabel(s2), BorderLayout.SOUTH);
       
        contentPane.add(new JLabel("Введите ЛСА:"),BorderLayout.NORTH);
        contentPane.add(jp1,BorderLayout.CENTER);
        contentPane.add(jp2,BorderLayout.SOUTH);
        
        frame.setSize(1000,160);
        frame.setLocation(300, 300);
        frame.setVisible(true);
        
      //Проверка введённого
        jb1.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
            	countSignals = 0;
            	Vertex = null; VertexSignals = null;
            	all = new ArrayList<String>();
            	allSignals = new ArrayList<String>();
            	//если поле пустое, значит ничего не делать!
            	if (!pole.getText().isEmpty()) {
            		checkOut(); //метод проверки введённого ЛСА
            		
            		System.out.println("Матрица связности: ");
                    outMatrix1();
                    System.out.println("Матрица соответствия вершин и сигналов: ");
                    outMatrix2();
            	} else {
            		showErrorMessage("Строка ввода ЛСА пуста");
            	}
            }
        });
        
      //Сохранить
        jb2.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
            	if (!pole.getText().isEmpty()) {
            		JFileChooser fc = new JFileChooser();
                    //в бинарном файле.
                    
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    File FS;
                    if (fc.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION){
                        FS = fc.getSelectedFile();
                        checkOut(); //метод проверки введённого ЛСА
                        if (Vertex != null) {
                        	try {
                                FileOutputStream fos = new FileOutputStream(FS);
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                
                                System.out.println("Матрица связности: ");
                                outMatrix1();
                                System.out.println("Матрица соответствия вершин и сигналов: ");
                                outMatrix2();
                                
                                oos.writeObject(Vertex);  //сохранение сначала матриц
                                oos.writeObject(VertexSignals);
                                oos.writeObject(allSignals); //потом контейнеров
                                oos.writeObject(pole.getText()); //потом контейнеров
                                
                                FS.createNewFile();
                            } catch (Exception ex) {}
                        }
                    }
            	} else {
            		showErrorMessage("Строка ввода ЛСА пуста");
            	}
            }
        });
        
      //Загрузить
        jb3.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
            	JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                File FS;
                if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
                    FS = fc.getSelectedFile();
                    try {
                        FileInputStream fis = new FileInputStream(FS);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                       
                        Vertex = (Integer[][]) ois.readObject();
                        VertexSignals = (Integer[][]) ois.readObject();
                        allSignals = (ArrayList<String>) ois.readObject();
                        String newLSA = (String) ois.readObject();
                        
                        pole.setText(newLSA);
                        
                        System.out.println("Матрица связности: ");
                        outMatrix1();
                        System.out.println("Матрица соответствия вершин и сигналов: ");
                        outMatrix2();
                    } catch (Exception ex) {}
                }
            }
        });
        
      //Очистка панели
        jb4.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
            	pole.setText(""); countSignals = 0;
            	Vertex = null; VertexSignals = null;
            	all = new ArrayList<String>();
            	allSignals = new ArrayList<String>();
            }
        });
        
      //Проверка на наличие висящих и недостижимых вершин.
        jb5.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
            	countSignals = 0;
            	Vertex = null; VertexSignals = null;
            	all = new ArrayList<String>();
            	allSignals = new ArrayList<String>();
            	//если поле пустое, значит ничего не делать!
            	if (!pole.getText().isEmpty()) {
            		checkOut(); //метод проверки введённого ЛСА
            		
            		checkVertexes();
            		
            		System.out.println("Матрица связности: ");
                    outMatrix1();
                    System.out.println("Матрица соответствия вершин и сигналов: ");
                    outMatrix2();
            	} else {
            		showErrorMessage("Строка ввода ЛСА пуста");
            	}
            }
        });
        
        //составление графа Мура.
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
                
                addVertex(); //изначальная инициализация (с нуля)
                Vertexes = "0";
                marked.add(0);
                int place = 0;
                for (int i = 0; i < Matrix.length; i++) {
                    if (Matrix[place][i] != null) {
                        try {
							make(i, "-", 0);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} //текущий блок, безусловно, из начала.
                        break;
                    }
                }
                
                System.out.println(Vertexes);

                jf = new JFrame("Мура");
                jf.setVisible(true);
                jf.setSize(500, 500);
                //создание меню
                JMenuBar mbar = new JMenuBar();
                JMenu file = new JMenu("FILE");
                file.add(save = new JMenuItem("Save"));
                file.add(load = new JMenuItem("Load"));
                mbar.add(file);
                addMenuLoadAndSave();

                //расположение на более верхнем уровне
                Container contentPane = jf.getContentPane();
                contentPane.setLayout(new BorderLayout());
                contentPane.add(mbar,BorderLayout.NORTH);

                formThisWay();
                jb7.setEnabled(true);
            }
        });
        
      //кодирование графа Мура.
        jb7.addActionListener( new ActionListener()
        {
            private Main aThis;

			public void actionPerformed(ActionEvent e) {
            	code = new CODE_MURA(MuraMatrix, Vertexes, aThis);
                code.setSize(400,400);
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
        		
        		jb9.setEnabled(true);
            }
        });
        
        jb9.addActionListener( new ActionListener()
        {
			public void actionPerformed(ActionEvent e) {
				System.out.println();
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
            }
        });
    }    
    
    private static void checkVertexes() {
    	boolean visjaw = false;
    	boolean nedost = false;
    	String s = "";
    	//нахождение висящих вершин
    	s += "Висящие вершины\n";
		for (int i = 0; i < Vertex.length - 1; i++) {
			boolean find1 = true;
			for (int j = 0; j < Vertex.length; j++) {
				if (Vertex[i][j] != null) {
					find1 = false;
				}
			}
			if (find1) {
				visjaw = true;
				s += " - Вершина № " + i + "\n";
			}
		}
		//нахождение недостижимых вершин
    	s += "Недостижимые вершины\n";
		for (int i = 1; i < Vertex.length; i++) {
			boolean find2 = true;
			for (int j = 0; j < Vertex.length; j++) {
				if (Vertex[j][i] != null) {
					find2 = false;
				}
			}
			if (find2) {
				nedost = true;
				s += " - Вершина № " + i + "\n";
			}
		}
		
		if (!nedost & !visjaw) {
			JOptionPane.showMessageDialog(null, "OK!", 
					"OK!", JOptionPane.INFORMATION_MESSAGE);
		} else {
			showErrorMessage(s);
		}
	}

	private static void outMatrix1() {
    	for (int i = 0; i < Vertex.length; i++) {
    		for (int j = 0; j < Vertex.length; j++) {
    			if (Vertex[i][j] == null) System.out.print("0 ");     			
    			else System.out.print(Vertex[i][j] + " ");
        	}
    		System.out.println();
    	}
    }
    
    private static void outMatrix2() {
    	if (VertexSignals != null) {
    		for (int i = 0; i < VertexSignals.length; i++) {
        		for (int j = 0; j < VertexSignals[i].length; j++) {
        			if (VertexSignals[i][j] == null) System.out.print("0 ");     			
        			else System.out.print(VertexSignals[i][j] + " ");
            	}
        		System.out.println();
        	}
    	}
    }
    
    //метод обработки кода
	private static void checkOut() {
		ArrayList<String> vertexBlock = new ArrayList<String>();
		
		//достаём строку
		String LSA = pole.getText();
		//разбиваем
		StringTokenizer tokens = new StringTokenizer(LSA," ");
		//вычисляем количество блоков
		int count = tokens.countTokens();
		//формируем первую матрицу
		Vertex = new Integer[count][count];
		
		//проверяем первый блок
		String firstBlock = tokens.nextToken();
		//если не начало, фэил
		boolean endExist = true;
		
		if (!LSA.endsWith("E")) {
			showErrorMessage("Нет конца");
			endExist = false;
		}
		
		if (firstBlock.contentEquals("B") & endExist) {
			Vertex[0][1] = 1; //из начала ведёт в следующий блок.
			//обходим каждый блок
			for (int i = 1; i < count; i++) {
				String nextBlock = tokens.nextToken();
				//если это блок операционный...
				if (nextBlock.contains("(")) {
					//да ещё и содержит прыжок!
					if (nextBlock.contains("^")) {
						StringTokenizer allow = new StringTokenizer(nextBlock, "^");
						//разбиваем на части, и если есть лишние прыжки...
						int ccc = allow.countTokens();
						if ((ccc > 2) | (ccc == 1)) {
							showErrorMessage("Слишком много прыжков");
						} else {
							//берём значение блока
							String block = allow.nextToken();
							//определяем блок на который он прыгает
							int jump = -1;
							try {
								jump = Integer.parseInt(allow.nextToken());
							} catch (NumberFormatException e) {}
							//если было введено чтото вроде ^1s или ^lol или ^^
							if (jump == -1) {
								//ошибка преобразования, там не число после прыжка.
								showErrorMessage("Неправильно набранный прыжок");
							//если было введено число чтото вроде ^656
							} else if (jump > count) {
								//не введено столько блоков.
								showErrorMessage("Столько блоков не существует");
							} else if (jump == i) {
								//не введено столько блоков.
								showErrorMessage("Нельзя зацикливать блок сам на себя");
							} else if (jump == 0) {
								//не введено столько блоков.
								showErrorMessage("Нельзя вести в НАЧАЛО, только в блок что после него");
							} else { //добавим связь и проверим текущий блок!
								//если в блоке операций содержится Х, то фэил
								if (!checkBlock(block,'y','Y', i)) {
									showErrorMessage("Это операционный блок, только Y");
								} else {
									Vertex[i][jump] = 1;
									vertexBlock.add(nextBlock);
								}
							}
						}
					//если блок не содержит безусловных переходов
					} else {
						if (!checkBlock(nextBlock,'y','Y', i)) {
							showErrorMessage("Это операционный блок, только Y");
						} else {
							Vertex[i][i+1] = 1;
							vertexBlock.add(nextBlock);
						}
					}
				} else if (!nextBlock.contentEquals("E")) { //значит это условный блок (если это не операционный блок)
					//если блок не содержит метков прыжков
					if (!nextBlock.contains("^")) {
						showErrorMessage("Нет переходов условия");
					} else {
						StringTokenizer allow = new StringTokenizer(nextBlock, "^");
						//разбиваем на части, и если есть лишние прыжки...
						int ccc = allow.countTokens();
						//если количество переходов не два (3 - это с учётом самого блока)
						if (ccc != 3) {
							showErrorMessage("Нет переходов условия");
						} else {
							//берём значение блока
							String block = allow.nextToken();
							//определяем блоки на которые он прыгает
							int jumpYES = -1, jumpNO = -1;
							try {
								jumpYES = Integer.parseInt(allow.nextToken());
								jumpNO = Integer.parseInt(allow.nextToken());
							} catch (NumberFormatException e) {}
							//если было введено чтото вроде ^1s или ^lol или ^^
							if ((jumpYES == -1) | (jumpNO == -1)) {
								//ошибка преобразования, там не число после прыжка.
								showErrorMessage("Неправильно набранный прыжок");
							//если было введено число чтото вроде ^656
							} else if ((jumpYES > count) | (jumpNO == count)) {
								//не введено столько блоков.
								showErrorMessage("Столько блоков не существует");
							} else if ((jumpYES == i) | (jumpNO == i)) {
								//не введено столько блоков.
								showErrorMessage("Нельзя зацикливать блок сам на себя");
							} else if ((jumpYES == 0) | (jumpNO == 0)) {
								//не введено столько блоков.
								showErrorMessage("Нельзя вести в НАЧАЛО, только в блок что после него");
							} else { //добавим связь и проверим текущий блок!
								//если в блоке операций содержится Х, то фэил
								if (!checkBlock(block,'x','X', i)) {
									showErrorMessage("Это условный блок, только Х");
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
			showErrorMessage("Нет начала");
		}

		//составление второй матрицы
		VertexSignals = new Integer[count][countSignals];
		
		for (int i = 0; i < all.size(); i++) {
			StringTokenizer ggg = new StringTokenizer(all.get(i),",");
			ggg.nextToken();
			int place = Integer.parseInt(ggg.nextToken());
			int place2 = Integer.parseInt(ggg.nextToken());
			VertexSignals[place2][place] = 1;
		}

		VertexSignals = null;
		System.out.println("all");
		System.out.println(Arrays.toString(all.toArray()));
		System.out.println("allSignals");
		System.out.println(Arrays.toString(allSignals.toArray()));



	}

	private static void showErrorMessage(String string) {
		JOptionPane.showMessageDialog(null, string, 
				"Ошибка", JOptionPane.ERROR_MESSAGE);
	}

	//обрабтка блока
	private static boolean checkBlock(String nextBlock, char c, char d, int blockNumber) {
		//если блок операций, убиваем скобки! 
		if (nextBlock.charAt(0) == '(') 
			nextBlock = nextBlock.substring(1, nextBlock.length() -1);
		StringTokenizer st = new StringTokenizer(nextBlock,",");
		int length = st.countTokens();
		
		//каждый сигнал обходим
		for (int i = 0; i < length; i++) {
			String s = st.nextToken();
			
			int place = allSignals.size();
			if (!allSignals.contains(s)) {
				countSignals++;
				allSignals.add(s);
			} else {
				place = allSignals.indexOf(s);
			}
			
			//сохранить для формирования матрицы сигналов.
			all.add(s+","+place+","+blockNumber);
			
			//если первый элемент сигнала, это маленький,Большой символ сигнала
			if ((s.charAt(0) == c) | (s.charAt(0) == d)) {
				s = s.substring(1); //вырезаем первый элемент и пытаемся обработать!
				try { //если всё что осталось от строки, это число, всё ок
					Integer.parseInt(s);
				//иначе false парсинга.
				} catch (NumberFormatException e) { return false; }
			} else { //первый элемент сигнала, не СИМВОЛ нужный нам!
				return false;
			}
		}
		return true;
	}
	
	////////////////////////////////////////////////////////////////
	/////////////////////////ВСЁ ЧТО КАСАЕТСЯ АВТОМАТА МУРА/////////
	////////////////////////////////////////////////////////////////
	
	//place - текущий блок
    //x - предыдущие иксы
    //was - блок от которого ведётся в текущий
    private static void make(int place, String x, int was) throws InterruptedException {
        //1. Определение типа связи
        if (place == Matrix.length - 1) { //если это определённо конец.
            MuraMatrix.get(was).set(0, x);
        } else if (LSA[place].contains("x")) { //если содержит Х, значит условие.
            if (x.contentEquals("-")) x = "";
                
            for (int i = 0; i < Matrix.length; i++) {
                if ((Matrix[place][i] != null) && (Matrix[place][i] == 2)) {
                    make(i, x + getX(LSA[place]), was);
                }
                if ((Matrix[place][i] != null) && (Matrix[place][i] == -2)) {
                    make(i, x + "!" + getX(LSA[place]), was);
                }
            }
        } else if (LSA[place].contains("y")) { //если содержит y
            if (marked.contains(place)) { //если уже был такой блок
                //ArrayList<String> ar = MuraMatrix.get(was); 
                MuraMatrix.get(was).set(marked.indexOf(place), x);
                //MuraMatrix.set(was, ar);
            } else {
                marked.add(place);
                addVertex(); //добавим новый узел
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

	//метод добавления строки в матрицу!
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

        for (int l = 0; l < MuraMatrix.size(); l++) {
            for (int j = 0; j < MuraMatrix.get(l).size(); j++) {
                System.out.print(MuraMatrix.get(l).get(j) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void formThisWay() {
        StringTokenizer st = new StringTokenizer(Vertexes, " ");
        LSA = new String[st.countTokens()];
        for (int i = 0; i < LSA.length; i++) {
            LSA[i] = st.nextToken();
        }

        //отрисовка стрелочек
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
            //настройки
            String settings = "ROUNDED;strokeColor=blue;fillColor=pink";
            Object[] v = new Object[MuraMatrix.size()]; //количество вершин состояний
            for (int i = 0; i < MuraMatrix.size(); i++) {
                    //распределяем вершины по кругу!
                    Point ptr = drawMiracle(i, MuraMatrix.size()); //находим положение вершины
                    v[i] = grap.insertVertex(parent, null,
                                    LSA[i]+"", ptr.x, ptr.y, 50,
                                    50, settings); //рисуем и выдаём ей имя!
            }
            for (int i = 0; i < MuraMatrix.size(); i++) {
                    for (int j = 0; j < MuraMatrix.size(); j++) {
                            if (!MuraMatrix.get(i).get(j).contains("0")) {
                                    //обходя матрицу связности графа
                                    //рисуем стрелочки между соответствующими вершинами!
                                    grap.insertEdge(parent, null,
                                                    MuraMatrix.get(i).get(j),
                                                    v[i], v[j]);
                            }
                    }
            }
        }
        finally
        {
            grap.getModel().endUpdate(); //обновление графа...
        }

        graphComponent = new mxGraphComponent(grap);
        jf.getContentPane().add(graphComponent);
    }

    private static Point drawMiracle(int i, int length) { //рисует чудеса
            //*************************************************
        // Преобразуем угол в радианы
        // 1 градус = pi/180 радиан
        //*************************************************
        double theta = ((i + 1) * 360/length) * (3.14/180);
        //*************************************************
        // Переводим в декартовы координаты
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
        //сохранение алгоритма
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
                            FS.delete(); //если файл существовал и там была инфа
                            //его убъёт совсем! и создаст новый
                            FS.createNewFile();
                            String s = "";
                            for (int i = 0; i < MuraMatrix.size(); i++) {
                                    for (int j = 0; j < MuraMatrix.size(); j++) {
                                            //собираем строку в один СТринг,
                                            //и добавляем разделители элементов
                                            s += MuraMatrix.get(i).get(j);
                                            //если это не последний элемент строки матрицы конечно
                                            if (j != MuraMatrix.get(i).size() - 1) { s += "_"; }
                                    }
                                    fw.write(s + "\r\n"); //записываем в файл с переводом на новую строку.
                                    s = ""; //очищаем буфер накапливания на запись
                            }
                            fw.write(Vertexes); //записать значение вершин1
                            fw.close(); //и закроем файл
                    } catch (Exception ex) {}
                }
            }
        });

        //Загрузка алгоритма
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
                        //разбивка строки на части определённые разделителем
                        st = new StringTokenizer(s, "_");
                        //колво разделённых токенов == размер матрицы, ведь так?
                        int leng = st.countTokens();
                        //создаём новую матрицу
                        MuraMatrix = new ArrayList<ArrayList<String>>();
                        for (int i1 = 0; i1 < leng; i1++) {
                            ArrayList<String> arr = new ArrayList<String>();
                            for (int i2 = 0; i2 < leng; i2++) {
                                arr.add("0");
                            }
                            MuraMatrix.add(arr);
                        }
                        for (int j = 0; j < leng; j++) {
                            MuraMatrix.get(0).set(j, st.nextToken()); //и записываем туда элементы
                        }
                        for (int i = 1; i < leng; i++) {
                            s = br.readLine();
                            st = new StringTokenizer(s, "_");
                            for (int j = 0; j < leng; j++) {
                                MuraMatrix.get(i).set(j, st.nextToken()); //и записываем туда элементы
                            }
                        }
                        Vertexes = br.readLine();
                        br.close();
                        jf.getContentPane().remove(graphComponent);
                        formThisWay(); //обновим всё на свете, чтобы оно работало
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
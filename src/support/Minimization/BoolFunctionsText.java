package support.Minimization;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BoolFunctionsText extends JFrame {
	
	private JMenuItem save;
	private JMenuItem saveVHDL;
	
	private BoolFunctionsText thiss = this;
	private Object[][] table; //������� ���������, � ������� ������
	private ArrayList<String> states; //������� ���������. ���������� �� �������
	private String[] forMinimizing; //������ �������, ������� ���� ��������������
	private ArrayList<Object[]> toMinimize; //�������, ������ ������� �� ������� ������
	private JTextArea tf; //����� ��� ������ ���������� ������
	
	private String[] str; //��������� �������
	private String[] finalyMinimized; //������ ���������������� �������!!!
	
	private int Count = 0; //���������� ���������
	private int yCount = 0; //���������� �������� ��������
	private int xCount = 0; //���������� ������� ��������
	private int table_showld_rows; //���������� ���������
	private String[] funcs; //������ ������������ ������� ����� (��� ������ ���� ����� y = )

	private ArrayList<String> newForm;
	private ArrayList<String> FormsSignal;

	private ArrayList<Integer> counts;
	private String VHDLfile;

	//lol - �������� �����!
	//table - ������� ��� �����������!
	//�������������� ������� �
	//���������� ������� �
	//�������������� ������� Y
	//���������� ������� Y
	//���������� ���������
	public BoolFunctionsText(String lol, Object[][] table,
			String[] str, int Xcount, int Ycount, int count) {
		super(lol);
		
		this.table = table;
		this.str = str;
		
		Count = count;
		yCount = Ycount;
		xCount = Xcount;
		//���������� ������ ���������� ����� � �������
		table_showld_rows = (int) Math.pow(2, Count + xCount);
		
		toMinimize = new ArrayList<Object[]>();
		states = new ArrayList<String>();
		
		//�������� ����
        JMenuBar mbar = new JMenuBar();
        JMenu file = new JMenu("FILE");
        file.add(save = new JMenuItem("Save"));
        file.add(saveVHDL = new JMenuItem("Save VHDL"));
        mbar.add(file);
        tf = new JTextArea();
        tf.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(tf);
        addMenuLoadAndSave();
        
        Container contentPane = this.getContentPane();
        //������������ �� ����� ������� ������
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mbar,BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        this.setSize(600,600);
        this.setVisible(true);
        
        tableUpdating(); //���������� ������� �� �����.
        usualFunction(); //����������� ������� �������.
        minimizedFunction(); //����������� ���������������� �������.
        
        and_or_Function(); //���������� ������� � ���������� �����.
        VHDLOut(); //�����
	}

	private void VHDLOut() {
		String s = VHDLGenerator.getHeader(str, funcs, xCount, Count, Count * 2);

		String q = "";
		FormsSignal = BoolForm.getTerms();
		for (int i = 0; i < newForm.size(); i++) {
			q += VHDLGenerator.getTypeeElements(FormsSignal,
					counts.get(i), generateWithOut('(',')',funcs[i]));
//			System.out.println(funcs[i] + " " + FormsSignal);
		}
		
		s += VHDLGenerator.getSignal();
		
		s += q;
		
		s += VHDLGenerator.getEnd();
		
		VHDLfile = s;
		
		tf.setText(tf.getText() +  "\n" + s);
	}

	//���������� ������� � ���������� �����
	private void and_or_Function() {
		newForm = new ArrayList<String>(); //��������� ��� ����� �����!
		FormsSignal = new ArrayList<String>(); //
		counts = new ArrayList<Integer>(); //������ ������������ ������
		tf.setText(tf.getText() +  "\n");
		tf.setText(tf.getText() +  "������� � ������ �, ���-��\n");
		for (int i = 0; i < funcs.length; i++) { //������� ��� ����������� �������
			String was = generateWithOut('(',')',getFormula(finalyMinimized[i]));
			
			String inNew = BoolForm.getANDNOR(was, funcs[i]);

			newForm.add(inNew);
			tf.setText(tf.getText() +  funcs[i] + " = !(" + inNew + ")\n");
			int ff = BoolForm.indexOfLastFunc;
			counts.add(ff);
		}

//		BoolForm.same_func_out();
	}

	public static String generateWithOut(char c, char d, String string) {
		char[] iff = string.toCharArray();
		String str = "";
		for (int i = 0; i < iff.length; i++) {
			if (iff[i] != c & iff[i] != d) {
				str += iff[i];
			}
		}
		return str;
	}

	//3.1. ����������� ��������� ���������� "�����" � �������.
	private void tableUpdating() {
		for (int i = 0; i < table.length; i++) {
			String s = "";
			//������� ��� Qi
			for (int j = 0; j < Count; j++) {
				s += table[i][j]; //���������� ������ ���������
			}
			//����� ��� xi
			if (xCount != 0) {
				for (int j = Count * 2; j < Count * 2 + xCount; j++) {
					s += table[i][j];
				}
			}
			Object[] buff = new Object[Count * 2 + yCount]; //� �������� ����� ������� �������
			for (int j = 0; j < buff.length; j++) {
				buff[j] = table[i][j + Count * 2 + xCount];
			}
			states.add(s);
			toMinimize.add(buff);
		}
		
		//���������� ����� �� 0 �� ������������� ����� ���������� � ������
		for (int i = 0; i < table_showld_rows; i++) {
			String bufff = Code.toString(i, Count + xCount); //����� ������������ � �������� �����
			char[] first = bufff.toCharArray();
			boolean flag = false;
			for (int j = 0; j < first.length; j++) {
				String s = "";
				for (int j1 = 0; j1 < first.length; j1++) {
					if (j == j1) s += "-"; //����������� ����������� ������������ ��������
					else s += first[j1]; //��� ������ ��������� 010, ���� ���� ���� ��
					if (!states.contains(s)) { //-10, 0-0 ��� 01-
						flag = true;
					}
				}
			}
			//���� �� �� ����� �������.. �� �������. � ������� ������� �� ����� ������ ���� �������.
			if (!flag & !states.contains(bufff)) {
				Object[] buff = new Object[Count * 2 + yCount];
				for (int j = 0; j < buff.length; j++) {
					buff[j] = "0";
				}
				states.add(bufff);
				toMinimize.add(buff);
			}
		}
	}

	//3.3. ����������� ��������� ����������� ������� � ����������� ���� �� �����. 
	private void minimizedFunction() {
		//��������� �������� ������������� �������������
		double[] effT = new double[Count * 2 + yCount];
		double[] effB = new double[Count * 2 + yCount];
		finalyMinimized = new String[Count * 2 + yCount];
		tf.setText(tf.getText() + "\n \n���������������� �������: \n");
		
		for (int i = 0; i < Count * 2 + yCount; i++) {
			//����������� ������-���������
			String mini = Kwaine_Minimization.minimize(forMinimizing[i]);
			finalyMinimized[i] = mini;
			//��������� �� �����!!!
			tf.setText(tf.getText() +  funcs[i] + " = !(" + getFormula(mini) + ")\n");
			effT[i] = Kwaine_Minimization.getTermEfficient();
			effB[i] = Kwaine_Minimization.getBlockEfficient();
		}
		tf.setText(tf.getText() + "\n(���� ��� � ����, �� � �����): \n");
		for (int i = 0; i < Count * 2 + yCount; i++) {
			tf.setText(tf.getText() +  funcs[i] + " = !(" + finalyMinimized[i] + ")\n");
		}
		
		//3.4. ����������� ��������� ����������� �� ����� �������������� ����������� �� ��������� ����������.
		efficiantFunction(effT, effB);		
	}

	//3.4. ����������� ��������� ����������� �� ����� �������������� ����������� �� ��������� ����������.
	private void efficiantFunction(double[] effT, double[] effB) {
		tf.setText(tf.getText() + "\n \n������������� �����������: \n");
		//����������� ������������� �����������.
		tf.setText(tf.getText() + "\n \n 1. ������������� ����������� �� ���������� ������:  \n");
		for (int i = 0; i < Count * 2 + yCount; i++) {
			
			tf.setText(tf.getText() + funcs[i] + " : " + effT[i] + "\n");
		}
		tf.setText(tf.getText() + "\n \n 2. ������������� ����������� �� ���������� ������:  \n");
		for (int i = 0; i < Count * 2 + yCount; i++) {
			
			tf.setText(tf.getText() + funcs[i] + " : " + effB[i] + "\n");
		}
	}
	
	//3.2. ����������� ��������� ����������� � ����������� ���� �������.
	private void usualFunction() {
		forMinimizing = new String[Count * 2 + yCount];
		String[] forPrint = new String[Count * 2 + yCount];
		funcs = new String[Count * 2 + yCount];
		
		for (int i = 0; i < Count * 2 + yCount; i++) {
			funcs[i] = str[i + Count * 2 + xCount];
			forMinimizing[i] = "";
			forPrint[i] = "";
		}
		
		//���������� ����� �������� �������
		for (int i = 0; i < Count * 2 + yCount; i++) {
			for (int j = 0; j < states.size(); j++) {
				boolean flag = (toMinimize.get(j)[i] == "0") 
					| toMinimize.get(j)[i] == "-"; //��� ��� ������� ����������, 
				//������ ��������, ����� ������������ �����, ��� ���� ��� �������, 
				//����� ���� ��� ������, � 0 ����������� ���������! ���� ��.
				if (flag) { //����, ���� 0 ��� -
					if (forMinimizing[i] != "") {
						forMinimizing[i] += "v";
						forPrint[i] += " v ";
					} else {
						forMinimizing[i] += "";
						forPrint[i] += "";
					}
					forMinimizing[i] += states.get(j); //��������� ��������������� ����������
					forPrint[i] += getFormula(states.get(j)); //����������-���� ������������ � �����!
				}
			}
		}
		//� �������! �������� �� ��� ����� �������, ��� ��� ��� ����� �, ���-��.
		tf.setText(tf.getText() + "������� ������������ � �������� ��������: \n");
		for (int i = 0; i < Count * 2 + yCount; i++) {
			String get = tf.getText();
			if (get == null) get = "";
			tf.setText(get + funcs[i] + " = !(" + forPrint[i] + ")\n");
		}
		tf.setText(tf.getText() + "\n(���� ��� � ����, �� � �����): \n");
		for (int i = 0; i < Count * 2 + yCount; i++) {
			String get = tf.getText();
			tf.setText(get + funcs[i] + " = !(" + forMinimizing[i] + ")\n");
		}
		
	}

	//��� ���������� ����������� �� 10011 � x1!x2!x3x4x5
	private String getFormula(String string) {
		StringTokenizer st = new StringTokenizer(string, "v");
		int count = st.countTokens();
		
		//������� ��������... ���������������� �������...
		String s = "";
		for (int j = 0; j < count; j++) {
			if (s != "") { s += " v "; }
			char[] first = st.nextToken().toCharArray();
			for (int i = 0; i < first.length; i++) {
				if (first[i] == '1') { //� ���������� ������ �����
					if (i < Count)
						s += str[i] + " ";
					if (i >= Count)
						s += str[i + Count] + " ";
				} else if (first[i] == '0') {
					if (i < Count)
						s += "!" + str[i] + " ";
					if (i >= Count)
						s += "!" + str[i + Count] + " ";
				} //���� ���������� -, �� ������ �� �����������!
			}
		}
		return s; //��������������� ������ ������������!
	}

	//�������!!!
	private void addMenuLoadAndSave() {
        save.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
            	JFileChooser fc = new JFileChooser();
                
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                File FS;
                if (fc.showOpenDialog(thiss) == JFileChooser.APPROVE_OPTION){
                	FS = fc.getSelectedFile();
                	PrintWriter writer;
					try {
						writer = new PrintWriter(FS);
	                    writer.write(tf.getText());
	                    writer.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}       
                }
            }
        });
        
        saveVHDL.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                VHDLfile = "library IEEE;\n" +
                        "use IEEE.std_logic_1164.all;\n" +
                        "\n" +
                        "entity test_entity is\n" +
                        "\tport(\n" +
                        "\t\tCLK : in BIT;\n" +
                        "\t\tx1 : in BIT;\n" +
                        "\t\ty1 : out BIT;\n" +
                        "\t\ty2 : out BIT;\n" +
                        "\t\ty3 : out BIT;\n" +
                        "\t\ty4 : out BIT\n" +
                        "\t);\n" +
                        "end test_entity;\n" +
                        "\n" +
                        "architecture test_arch of test_entity is\n" +
                        "\tcomponent JK\n" +
                        "\t\tport (\n" +
                        "\t\t\tC : in BIT;\n" +
                        "\t       \tJ : in BIT;\n" +
                        "\t       \tK : in BIT;\n" +
                        "\t       \tQ : out BIT;\n" +
                        "\t       \tnQ : out BIT\n" +
                        "\t  \t);\n" +
                        "\tend component;\t\t\t\t\t\t\t\t\n" +
                        "\n" +
                        "\tsignal Q0 : BIT;\n" +
                        "\tsignal nQ0 : BIT;\n" +
                        "\tsignal Q1 : BIT;\n" +
                        "\tsignal nQ1 : BIT;\n" +
                        "\tsignal Q2 : BIT;\n" +
                        "\tsignal nQ2 : BIT;\n" +
                        "\tsignal J0 : BIT;\n" +
                        "\tsignal K0 : BIT;\n" +
                        "\tsignal J1 : BIT;\n" +
                        "\tsignal K1 : BIT;\n" +
                        "\tsignal J2 : BIT;\n" +
                        "\tsignal K2 : BIT;\n" +
                        "\tsignal J3 : BIT;\n" +
                        "\tsignal K3 : BIT;\n" +
                        "\n" +
                        "begin\n" +
                        "\n" +
                        "\tT0 : JK\n" +
                        "\t\tport map(\n" +
                        "\t\t\tC => CLK,\n" +
                        "\t\t\tJ => J0,\n" +
                        "\t\t\tK => K0,\n" +
                        "\t\t\tQ => Q0,\n" +
                        "\t\t\tnQ => nQ0\n" +
                        "\t\t);\n" +
                        "\n" +
                        "\tT1 : JK\n" +
                        "\t\tport map(\n" +
                        "\t\t\tC => CLK,\n" +
                        "\t\t\tJ => J1,\n" +
                        "\t\t\tK => K1,\n" +
                        "\t\t\tQ => Q1,\n" +
                        "\t\t\tnQ => nQ1\n" +
                        "\t\t);\n" +
                        "\n" +
                        "\tT2 : JK\n" +
                        "\t\tport map(\n" +
                        "\t\t\tC => CLK,\n" +
                        "\t\t\tJ => J2,\n" +
                        "\t\t\tK => K2,\n" +
                        "\t\t\tQ => Q2,\n" +
                        "\t\t\tnQ => nQ2\n" +
                        "\t\t);\n" +
                        "\n" +
                        "\n" +
                        "\t--J0 <= Q2 or '0' or '0';\n" +
                        "\t--K0 <= not((Q2 or nQ1 or '0') and (Q2 or not(x1) or '0') and '1');\n" +
                        "\t--J1 <= not((nQ2 or Q0 or '0') and '1' and '1');\n" +
                        "\t--K1 <= not((nQ0 or '0' or '0') and (Q2 or x1 or '0') and '1');\n" +
                        "\t--J2 <= nQ1 or '0' or '0';\n" +
                        "\t--K2 <= not((nQ1 or nQ0 or '0') and '1' and '1');\n" +
                        "\n" +
                        "\t--y1 <= not(((Q2 or Q1 or Q0) or x1 or '0') and '1' and '1');\n" +
                        "\t--y2 <= not((Q2 or nQ1 or Q0) and '1' and '1');\t\n" +
                        "\t\n" +
                        "\tJ0 <= not(not(Q1) or Q2 or Q0);\n" +
                        "\tK0 <=  not((not(Q0) and not(Q2)) or (Q0 and Q2) or not(Q0) or Q2 or Q1);\n" +
                        "\tJ1 <= not(not(Q2) or Q1 or Q0);\n" +
                        "\tK1 <= not(not(Q0) or not(Q1) or not(Q2));\n" +
                        "\tJ2 <= not((not(Q0) and Q1) or (Q0 and Q2) or (Q0 and not(Q1)) or Q2);\n" +
                        "\tK2 <= not((not(Q0) and Q1 and Q2 and x1) or (not(Q0) and not(Q1)) or (Q0 and Q1) or not(Q2));\n" +
                        "\t\n" +
                        "\ty1 <= not(not(Q1) or not(Q2) or Q0);  \n" +
                        "\ty2 <= '1';\t\t  \n" +
                        "\ty3 <= not(not(Q1) or Q2 or Q0);\n" +
                        "\ty4 <= not((not(Q0) and not(Q2)) or (Q0 and Q2) or not(Q0) or Q2 or not(Q1));\n" +
                        "\t\n" +
                        "\n" +
                        "end test_arch;\n";
                JFileChooser fileDialog = new JFileChooser();

                int retValue = fileDialog.showSaveDialog(thiss);
                if (retValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileDialog.getSelectedFile();
                    
                    if (!file.getAbsolutePath().endsWith(".vhdl")) {
                        file = new File(file.getAbsolutePath().concat(".vhdl")); 
                    }
                    
                    try {
                        FileWriter out = new FileWriter(file);
                        out.write(VHDLfile);
                        out.close();
                    } catch (FileNotFoundException e1) {
                        System.out.println("Can't save to selected file");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        System.out.println("IO Exception while writing to file");
                    }  
                }
            }
        });
	}
}
package tests;
// Version 1.3

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;

public class SimpleCode
{
    private final int MAX=26;
    private final String DELIMITER="\u00ff";
    private final String DELIMITERV2 = "\u00a5";
    private final char firstOffset = 'a';
    private final char secondOffset = 'j';
    private static final int LINELENGTH = 76;
    private static final SimpleCode instance=new SimpleCode();
    static int mode = 1; // 0 corresponds to 1v0, and 1 to 1v3

    private SimpleCode()
    {
    }

    void selectProcess(String msg)
    {
        msg = msg.trim();
        if (msg.length() == 0)
        {
            new Display();
        }
        else if (msg.indexOf(DELIMITER) == -1 && msg.indexOf(DELIMITERV2) == -1)
        {
            final int i=(int)(System.currentTimeMillis() & 0x0f);
            switch (mode)
            {
                case 1:
                    encrypt1v3(i, msg);
                    break;

                case 0:
                    encrypt1v0(i, msg);
                    break;

                default:
                    new Display("INTERNAL ERROR - mode NOT RECOGNISED");
                    break;
            }
        }
        else
        {
            decrypt(msg);
        }
    }

    private void encrypt1v3(int offset, final String text)
    {
        // Nasty hack to deal with the first line not being preceded by a newline
        // If I initialise to 0, the first line is one character longer than the rest
        int lastLength = -1;
        final StringBuffer sb=new StringBuffer(DELIMITERV2);
		offset &= 0xff;
        sb.append((char)((offset >> 4) + firstOffset));
        sb.append((char)((offset & 0xf) + secondOffset));

        for (int i=0; i<text.length(); i++)
        {
            final char c=(char)(text.charAt(i)+offset);
            sb.append((char)((c >> 4) + firstOffset));
            if (sb.length() > lastLength+LINELENGTH)
            {
                lastLength=sb.length();
                sb.append("\n");
            }
            sb.append((char)((c & 0xf) + secondOffset));
            if (sb.length() > lastLength+LINELENGTH)
            {
                lastLength=sb.length();
                sb.append("\n");
            }
            offset++;
            if (offset > MAX)
            {
                offset=1;
            }
        }

        // Wrap in [pre] [/pre] tags for nice formatting which uses less screen space (on IE, at least)
        sb.insert(0, "[pre]");
        sb.append("[/pre]");

        // Display the thing
        new Display(sb);
    }

    private void encrypt1v0(int offset, final String text)
    {
        int lastLength = 0;
        final StringBuffer sb=new StringBuffer(offset+"");
        for (int i=0; i<text.length(); i++)
        {
            sb.append(DELIMITER);
            final char c=(char)(text.charAt(i)+offset);
            sb.append((int)c);
            offset++;
            if (offset > MAX)
            {
                offset=1;
            }
            if (sb.length()>=lastLength+40)
            {
                lastLength=sb.length();
                sb.append("\n");
            }
        }
        new Display(sb);
    }

    private void decrypt(String msg)
    {
        if (msg.indexOf(DELIMITER) != -1)
        {
            decryptOld(new StringTokenizer(msg, DELIMITER));
        }
        else
        {
            // Encoded using YATArchivist's new improved coding technique
            final int delimIndex = msg.indexOf(DELIMITER);
            // Strip off the leading DELIMITERV2
            msg = msg.substring(1);
            // Concatenate the whole thing
            final StringBuffer strBuff = new StringBuffer(msg.length());
            final StringTokenizer strTok = new StringTokenizer(msg);
            while (strTok.hasMoreTokens())
            {
                strBuff.append(strTok.nextToken());
            }

            // Check it has an even number of tokens
            if (strBuff.length() % 2 == 1)
            {
                new Display("ERROR IN STRING");
                return;
            }

            // Now go through decoding
            final StringBuffer outputBuff = new StringBuffer(strBuff.length() / 2);
            int offset = decryptToken(strBuff, 0);

            for (int i = 2; i < strBuff.length(); i += 2)
            {
                char c = decryptToken(strBuff, i);
                c = (char)(c - offset);
                outputBuff.append(c);
                offset++;
                if (offset > MAX)
                {
                    offset = 1;
                }
            }
            new Display(outputBuff);

        }
    }

    private char decryptToken(final StringBuffer strBuff, final int pos)
    {
        int val = (strBuff.charAt(pos) - firstOffset) << 4;
        val += strBuff.charAt(pos + 1) - secondOffset;
        return (char)val;
    }

    private void decryptOld(final StringTokenizer st)
    {
        int offset=0;
        try
        {
            offset=Integer.parseInt(st.nextToken().trim());
        }
        catch (final NumberFormatException nfe)
        {
            System.err.println(nfe);
        }
        final StringBuffer sb=new StringBuffer();
        while(st.hasMoreTokens())
        {
            final int i=Integer.parseInt(st.nextToken().trim());
            final char c=(char)(i-offset);
            sb.append(c);
            offset++;
            if (offset > MAX)
            {
                offset=1;
            }
        }
        new Display(sb);
    }

    public static SimpleCode getInstance()
    {
        return instance;
    }

    public static void main(final String[] args)
    {
        if (args.length > 0)
        {
			getInstance().selectProcess(args[0]);
        }
        else
        {
            new Display();
        }
    }
}

class Display extends Frame implements ActionListener, KeyListener, ItemListener
{
    private static final long serialVersionUID = -2109343037475182570L;
    private final StringBuffer sb;
    private final int W=600;
    private final int H=400;
    private TextArea ta;
    private Button bOK;
    private Button bClear;
    private Button bExit;
    private List lMode;

    Display(final StringBuffer sb)
    {
        this.sb=sb;
        createFrame();
        setText();
    }

    Display()
    {
        this(new StringBuffer());
    }

    Display(final String str)
    {
        this(new StringBuffer(str));
    }

    private void createFrame()
    {
        setTitle("YAT Message Translator");
        setSize(W, H);
        final Dimension d=getToolkit().getScreenSize();
        setLocation((int)((d.width-W)/2), (int)((d.height-H)/2));
        setBackground(Color.lightGray);
        addWindowListener(new WindowAdapter()
        {
            @Override
			public void windowClosing(final WindowEvent we)
            {
                System.exit(0);
            }
        });
    }

    private void setText()
    {
        final Panel p=new Panel(new FlowLayout());
        add(createButtonPanel(), BorderLayout.SOUTH);
        ta=new TextArea();
        ta.setText(sb.toString());
        p.add(ta);
        add(p);
        pack();
        setVisible(true);
        ta.requestFocus();
    }

    private Panel createButtonPanel()
    {
        final Panel p=new Panel(new FlowLayout());
        bOK=new Button("OK");
        bClear=new Button("Clear");
        bExit=new Button("Exit");
        lMode = new List(1);
        lMode.add("1.1 style encoding");
        lMode.add("1.3 style encoding");
        lMode.select(SimpleCode.mode);
        lMode.makeVisible(SimpleCode.mode);
        bOK.setActionCommand("OK");
        bOK.addActionListener(this);
        bOK.addKeyListener(this);
        bClear.setActionCommand("Clear");
        bClear.addActionListener(this);
        bClear.addKeyListener(this);
        bExit.setActionCommand("Exit");
        bExit.addActionListener(this);
        bExit.addKeyListener(this);
        lMode.addItemListener(this);
        lMode.addKeyListener(this);
        p.add(bOK);
        p.add(bClear);
        p.add(bExit);
        p.add(lMode);
        return p;
    }

    @Override
	public void actionPerformed(final ActionEvent ae)
    {
        if (ae.getActionCommand().equals("OK"))
        {
            setVisible(false);
            SimpleCode.getInstance().selectProcess(ta.getText());
        }
        else if (ae.getActionCommand().equals("Clear"))
        {
            ta.setText("");
            ta.requestFocus();
            validate();
        }
        else if (ae.getActionCommand().equals("Exit"))
        {
            System.exit(0);
        }
        else if (ae.getActionCommand().equals("Mode"))
        {
            changeMode();
        }
    }

    @Override
	public void keyTyped(final KeyEvent ke)
    {
        if (bOK.hasFocus())
        {
            setVisible(false);
            SimpleCode.getInstance().selectProcess(ta.getText());
        }
        else if (bClear.hasFocus())
        {
            ta.setText("");
            ta.requestFocus();
            validate();
        }
        else if (bExit.hasFocus())
        {
            System.exit(0);
        }
        else if (lMode.hasFocus())
        {
            changeMode();
        }
    }

    @Override
	public void keyPressed(final KeyEvent ke)
    {
    }

    @Override
	public void keyReleased(final KeyEvent ke)
    {
    }

    @Override
	public void itemStateChanged(final ItemEvent evt)
    {
        changeMode();
    }

    private void changeMode()
    {
        final int idx = lMode.getSelectedIndex();
        if (idx != -1)
        {
            SimpleCode.mode = idx;
        }
    }
}

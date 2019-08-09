// Intro: Play an interactive game of don't click the white tiles by not clicking the white tiles but clicking the black tiles.
// Name: Jing Xuan Long
// Due: January 22 2016
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import hsa.*;
import java.applet.*;
import java.net.*;

// The "PianoX" class.
public class PianoX extends Frame
{
    AudioClip ding, a, b, c, d, e, f, g, welcome, lose; //sound variables
    int[] tileY = new int [5]; //keeps track of each tile row's y position
    int[] pickTile = new int [5]; //keeps track of which tile was assigned to each row
    boolean[] grayTile = new boolean [5]; //keeps track of which tile was clicked on
    boolean gameStart, clicked; //flags if clicked/game is active
    int speed, total, count, score, highScore, mx; //game variables
    Image tile1, tile2, tile3, tile4, start, blank, gray1, gray2, gray3, gray4, offScreenImage; //image variable
    Graphics offScreenBuffer;
    public PianoX ()
    {
	super ("PianoX");       // Set the frame's name
	setSize (600, 800);     // Set the frame's size
	Dimension dim = Toolkit.getDefaultToolkit ().getScreenSize ();
	this.setLocation (dim.width / 2 - this.getSize ().width / 2, dim.height / 2 - this.getSize ().height / 2); //centers game in moniter
	setResizable (false);
	ding = Applet.newAudioClip (getCompleteURL ("ding.wav"));
	welcome = Applet.newAudioClip (getCompleteURL ("welcome.wav"));
	lose = Applet.newAudioClip (getCompleteURL ("lose.wav"));
	a = Applet.newAudioClip (getCompleteURL ("a.wav"));
	b = Applet.newAudioClip (getCompleteURL ("b.wav"));
	c = Applet.newAudioClip (getCompleteURL ("c.wav"));
	d = Applet.newAudioClip (getCompleteURL ("d.wav"));
	e = Applet.newAudioClip (getCompleteURL ("e.wav"));
	f = Applet.newAudioClip (getCompleteURL ("f.wav"));
	g = Applet.newAudioClip (getCompleteURL ("g.wav"));
	welcome.play ();
	show ();                // Show the frame
	MediaTracker tracker = new MediaTracker (this); //Fetch images
	blank = Toolkit.getDefaultToolkit ().getImage ("blank.png");
	tracker.addImage (blank, 0);
	start = Toolkit.getDefaultToolkit ().getImage ("start.png");
	tracker.addImage (start, 0);
	tile1 = Toolkit.getDefaultToolkit ().getImage ("tile1.png");
	tracker.addImage (tile1, 0);
	tile2 = Toolkit.getDefaultToolkit ().getImage ("tile2.png");
	tracker.addImage (tile2, 0);
	tile3 = Toolkit.getDefaultToolkit ().getImage ("tile3.png");
	tracker.addImage (tile3, 0);
	tile4 = Toolkit.getDefaultToolkit ().getImage ("tile4.png");
	tracker.addImage (tile4, 0);
	gray1 = Toolkit.getDefaultToolkit ().getImage ("gray1.png");
	tracker.addImage (gray1, 0);
	gray2 = Toolkit.getDefaultToolkit ().getImage ("gray2.png");
	tracker.addImage (gray2, 0);
	gray3 = Toolkit.getDefaultToolkit ().getImage ("gray3.png");
	tracker.addImage (gray3, 0);
	gray4 = Toolkit.getDefaultToolkit ().getImage ("gray4.png");
	tracker.addImage (gray4, 0);
	//  Wait until all of the images are loaded
	try
	{
	    tracker.waitForAll ();
	}
	catch (InterruptedException e)
	{
	}
	try
	{
	    BufferedReader n = new BufferedReader (new FileReader ("Highscore.txt"));
	    highScore = Integer.parseInt (n.readLine ());

	}
	catch (IOException e)
	{
	}
	startgame ();
    } // Constructor


    //paint
    //draws the game
    //called at game start and each time a tile changes postition
    public void paint (Graphics g)
    {
	if (offScreenBuffer == null)
	{
	    offScreenImage = createImage (size ().width, size ().height);
	    offScreenBuffer = offScreenImage.getGraphics ();
	}
	offScreenBuffer.setFont (new Font ("Calbri", Font.BOLD, 25));
	for (int i = 0 ; i <= 4 ; i++) //goes through and draws each row of tile (5)
	{
	    if (pickTile [i] == 1 && grayTile [i] == false)
		offScreenBuffer.drawImage (tile1, 0, tileY [i], this);
	    else if (grayTile [i] == true && pickTile [i] == 1)
		offScreenBuffer.drawImage (gray1, 0, tileY [i], this);
	    if (pickTile [i] == 2 && grayTile [i] == false)
		offScreenBuffer.drawImage (tile2, 0, tileY [i], this);
	    else if (grayTile [i] == true && pickTile [i] == 2)
		offScreenBuffer.drawImage (gray2, 0, tileY [i], this);
	    if (pickTile [i] == 3 && grayTile [i] == false)
		offScreenBuffer.drawImage (tile3, 0, tileY [i], this);
	    else if (grayTile [i] == true && pickTile [i] == 3)
		offScreenBuffer.drawImage (gray3, 0, tileY [i], this);
	    if (pickTile [i] == 4 && grayTile [i] == false)
		offScreenBuffer.drawImage (tile4, 0, tileY [i], this);
	    else if (grayTile [i] == true && pickTile [i] == 4)
		offScreenBuffer.drawImage (gray4, 0, tileY [i], this);
	}
	offScreenBuffer.drawString ("" + (score), 293, 150);
	if (gameStart == false) //if the game hasn't started, draw the landing page
	{
	    offScreenBuffer.drawImage (start, 0, 0, this);
	    offScreenBuffer.drawString ("" + (highScore), 517, 710);
	}
	g.drawImage (offScreenImage, 0, 0, this);
	// Place the drawing code here
    } // paint method


    public boolean handleEvent (Event evt)
    {
	if (evt.id == Event.WINDOW_DESTROY)
	{
	    System.exit (0);
	}
	return super.handleEvent (evt);
    }


    // We need to override update when using the offScreenBuffer
    // To prevent the automatic clearing of the screen
    public void update (Graphics g)
    {
	paint (g);
    }


    // Gets the URL needed for newAudioClip
    public URL getCompleteURL (String fileName)
    {
	try
	{
	    return new URL ("file:" + System.getProperty ("user.dir") + "/" + fileName);
	}
	catch (MalformedURLException e)
	{
	    System.err.println (e.getMessage ());
	}
	return null;
    }


    //pianosound
    //randomly picks note to play
    //called when tile has been clicked
    public void pianosound ()
    {
	int sound = (int) (Math.random () * 7);

	if (sound == 0)
	    a.play ();
	if (sound == 1)
	    b.play ();
	if (sound == 2)
	    c.play ();
	if (sound == 3)
	    d.play ();
	if (sound == 4)
	    e.play ();
	if (sound == 5)
	    f.play ();
	if (sound == 6)
	    g.play ();
    }


    //death
    //ends the game amd compares score with highscore. Also asks user if they want to play again and resets variables
    //called when black tile was missed or blacktile dissapears
    public void death ()
    {
	lose.play ();
	highScore = Math.max (score, highScore);
	TextOutputFile n = new TextOutputFile ("Highscore.txt");
	n.println (highScore);
	n.close ();
	gameStart = false;
	int reply = JOptionPane.showConfirmDialog (null, "Play again?", "Score: " + (score), JOptionPane.YES_NO_OPTION);
	if (reply == JOptionPane.YES_OPTION)
	{
	    startgame ();
	}
	else
	{
	    System.exit (0);
	}
    }


    //startgame
    //starts game threads when user clicks landing page
    //called when you launch the game and when you say yes to play again
    public void startgame ()
    {
	clicked = false;
	tilegen gen = new tilegen ();
	move go = new move ();
	clicks clicker = new clicks ();
	do
	{
	    if (clicked == true)
	    {
		pianosound ();
		offScreenBuffer.drawImage (blank, 0, 0, this);
		gen.start ();
		go.start ();
		clicker.start ();
		gameStart = true;
		clicked = false;
	    }
	}
	while (gameStart == false);
    }


    //mouseDown
    //records x position of where you clicked and tells game that you clicked
    //called when you click
    public boolean mouseDown (Event evt, int x, int y)
    {
	mx = x;
	return clicked = true;
    }


    //assigns each row a random tile
    public class tilegen extends Thread
    {
	public void run ()
	{
	    speed = 2;
	    score = 0;
	    count = 0;
	    total = 0;
	    for (int i = 0 ; i <= 4 ; i++) //sets the initial position of each row of tiles (offscreen in the negatives)
	    {
		pickTile [i] = 0;
		grayTile [i] = false;
		tileY [i] = -200 + i * -200;
	    }
	    while (gameStart == true)
	    {
		for (int i = 0 ; i <= 4 ; i++)
		{
		    if (pickTile [i] == 0) //checks if the row of tile doesn't have a tile assigned to it and assigns one for it
		    {
			pickTile [i] = (int) (Math.random () * 5);
		    }
		    if (tileY [i] > 800) //check if row of tile should dissapear and resets the row
		    {
			pickTile [i] = 0;
			tileY [i] = -200;
			grayTile [i] = false;
			total++;
		    }
		}
	    }
	}
    }


    //moves the y position of each row of tiles and repaints each second. Also changes the speed of moving tiles at milestones.
    public class move extends Thread
    {
	public void run ()
	{

	    while (gameStart == true)
	    {
		if (score == 25)
		{
		    speed = 3;
		}
		else if (score == 50)
		{
		    speed = 4;
		}
		else if (score == 75)
		{
		    speed = 5;
		}
		else if (score == 100)
		{
		    speed = 6;
		}
		else if (score == 150)
		{
		    speed = 7;
		}
		for (int i = 0 ; i <= 4 ; i++) //goes through each row and adds the speed variable to the y position which moves the row of tile
		{
		    tileY [i] += speed;
		}
		try
		{
		    Thread.sleep (5);
		}
		catch (InterruptedException ex)
		{
		    Thread.currentThread ().interrupt ();
		}
		repaint ();
	    }
	}
    }


    //if you click, this thread checks if you gain a point or die.
    public class clicks extends Thread
    {
	public void run ()
	{
	    while (gameStart == true)
	    {
		while (score >= total) //each time a tile passes through the end, it adds to the total. If your score is ever lower than the total it means that you let one pass without clicking.
		{
		    while (clicked == true)
		    {
			if (pickTile [count] == 1)
			{
			    if (mx >= 0 && mx <= 150) //if the position of x is correct for the 1st tile of the 4
			    {
				score++;
				pianosound ();
				grayTile [count] = true; //tells game that the tile should turn gray
				count++;
			    }
			    else
			    {
				death ();
				Thread.currentThread ().interrupt ();
				return;
			    }
			}
			else if (pickTile [count] == 2)
			{
			    if (mx >= 151 && mx <= 300)
			    {
				score++;
				pianosound ();
				grayTile [count] = true;
				count++;
			    }
			    else
			    {
				death ();
				Thread.currentThread ().interrupt ();
				return;
			    }
			}
			else if (pickTile [count] == 3)
			{
			    if (mx >= 301 && mx <= 450)
			    {
				score++;
				pianosound ();
				grayTile [count] = true;
				count++;
			    }
			    else
			    {
				death ();
				Thread.currentThread ().interrupt ();
				return;
			    }
			}
			else if (pickTile [count] == 4)
			{
			    if (mx >= 451 && mx <= 600)
			    {
				score++;
				pianosound ();
				grayTile [count] = true;
				count++;
			    }
			    else
			    {
				death ();
				Thread.currentThread ().interrupt ();
				return;
			    }
			}
			clicked = false;
			if (count == 5)
			    count = 0;
		    }
		}
		death ();
		Thread.currentThread ().interrupt ();
		return;
	    }
	}
    }


    public static void main (String[] args)
    {
	new PianoX ();  // Create a PianoX frame

    } // main method
} // PianoX class



# YouTube View History Extractor

The purpose of this repo is to offer a program that extracts useful information from YouTube View History data

**Note**: View_History_Extractor.java needs OSU components to execute. You can find it in the lib folder or download [here](https://cse22x1.engineering.osu.edu/common/components.jar). If you are interested, you may look over [OSU API](https://cse22x1.engineering.osu.edu/common/doc/)

## How to Extract your data

### Step 1: Download your data

1. Go to YouTube webpage.
2. Login to your google account.
3. Click your profile picture icon at the right upper corner of your screen.
4. Click "Your data in YouTube"
5. Click "Download YouTube data"

### Step 2: Set up input file

After you successfully download your data, unzip the file wherever you want. View_History_Extractor.java is designed to extract data from view history data file. The file we're looking for is `Takeout` -> `YouTube & YouTube Music` -> `View History` -> `View History.html`.
This format is not guaranteed, but your file would have a similar one. 

As you found the file, We need to copy the pathway of your file.
- Right-click on the file, hold down the Shift key, and select "Copy as path" from the context menu 
- OR left-click on the file, and use the keyboard shortcut **Ctrl+Shift+C**. 
Either way is fine.

Then 
- Open `View_History_Extractor.java`
- Scorll down to the end of the file
- Find the line as below and delete `"data/시청 기록.html"`
- Replace there with what you just copied(paste).
```java
public static void main(String[] args) { // Before replacement
        SimpleReader in = new SimpleReader1L("data/시청 기록.html");
```
```java
public static void main(String[] args) { // After replacement
        SimpleReader in = new SimpleReader1L("C:/Users/user/Downloads/Takeout/YouTube & YouTube Music/View History/View History.html"); // Example pathway
```

### Step 3: Set up output file

You need to set up where the output would be placed before execution as well. Decide which folder to store your extracted data and copy the pathway of the folder with the same method described in the previous step.

Again, the paste process is similar, too.
- Open `View_History_Extractor.java`
- Scorll down to the end of the file
- Find the line as below and delete `"output"`
- Replace there with what you just copied(paste).
```java
dataCleaning(in, link, title, channelLink, channelName, time); // extract youtube data
String folder = "output";
SimpleWriter out = new SimpleWriter1L(folder + "/YouTube_View_History.csv");
dataFraming(out, link, title, channelLink, channelName, time); // print data into text file
```
```java
dataCleaning(in, link, title, channelLink, channelName, time); // extract youtube data
String folder = "C:/Users/user/Downloads/Takeout/YouTube & YouTube Music/View History";
SimpleWriter out = new SimpleWriter1L(folder + "/YouTube_View_History.csv");
dataFraming(out, link, title, channelLink, channelName, time); // print data into text file
```

### Step 4: Adjust year and time zone

To extract your viewing time data, we need to make sure to change the year and time zone to check befor execution. Go to the line between 40 and 50. You would find a snippet of code as below
```java
if (time.equals("2024. ") || time.equals("2025. ")) { 
                    while (!time.contains("KST")) {
                        time += in.read();
                    }
```
What we need to focus on are three: `2024. `, `2025. `, and `KST`.
Depends on your data, it is your job to change the years and time zone appropriately.
Below is an example:
```java
if (time.equals("1587. ") || time.equals("1588. ")) { 
                    while (!time.contains("EST")) {
                        time += in.read();
                    }
```

### Step 5: RUN IT!

You are good to extract your data now. Run the program and enjoy your data.


**Note**: There are some cases that the created csv files do not show titles correctly or have other kinds of problems. It is possible that the problems are on Excel itself, rather than a malfunctioning of the program. I suggest you to browse any similar cases of your issue first, and contact `junghc.801@gmail.com` if you cannot find one.

[components-jar]: https://cse22x1.engineering.osu.edu/common/components.jar
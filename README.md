# YouTube View History Extractor

The purpose of this repo is to offer a program that extracts useful information from YouTube View History data

## 

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

### 







**Note**: if you're using VSCode for class projects, you might be wondering
why you never had to do this. In general, it's bad practice to commit binaries
to version control. However, we have no way of managing dependencies with the
custom `components.jar`, so I included them directly in the template. I did not
include them here, so you could see how it might be done from scratch. If at any
point you're struggling with Step 3, just copy the lib folder from the monorepo
template.

## Next Steps

<!-- TODO: navigate to part 1 of the portfolio project and delete this comment -->

Now that you have everything setup, you can begin crafting your component. There
will be deadlines for each step in Carmen, but you're free to complete each step
as early as you'd like. To start, you'll want to visit the [doc](doc/) directory
for each assignment file.

[components-jar]: https://cse22x1.engineering.osu.edu/common/components.jar
[jdk-downloads]: https://www.oracle.com/java/technologies/downloads/
[use-this-template]: https://github.com/new?template_name=portfolio-project&template_owner=jrg94
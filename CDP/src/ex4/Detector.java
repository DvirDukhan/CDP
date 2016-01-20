import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Detector
{
	// User local temp folder
  private static final Path TEMP_PATH = new Path("temp");
  private static final Path TEMP_PATH2 = new Path("temp2");
  
  /**
   * This Mapper gets a <DocName,DocCotent> and returns for each Ngram in the document  <Ngram$DocName,1>  
   * @author Dvir
   *
   */
  public static class NGramMapper	extends Mapper<Text, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private final Text NGram = new Text();
		private String stringNgram ="";
		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			int n =  context.getConfiguration().getInt("n", 1);
			String inputString = value.toString();
			//exlude all non chars and numbers and spaces from the string and move is to lower case
			String handledString = inputString.replaceAll("[^a-zA-Z0-9 ]"," ").replaceAll("\\s+"," ").toLowerCase();			
			String[] splitedStrings = handledString.split(" ");
			
			
			//document is too short for this Ngram.

			if(splitedStrings.length<n)
			{
				return;
			}

			for (int i =0; i<= splitedStrings.length-n; i++ )
			{
				//create Ngram
				for (int j=i; j< i+n; j++)
				{
					stringNgram = stringNgram + splitedStrings[j] + " ";
				}

				String filename = key.toString();
				NGram.set(stringNgram.substring(0, stringNgram.length()-1)+"$"+filename);
				//Ngram is now Ngram$docName
				context.write(NGram, one);
				stringNgram="";
			}
		}
	}

  
  

  /**
   * This reducer gets a <Ngram$DocName, list of 1'> and returns <Ngram$DocName, sum of appearances>
   * as shown in class
   * @author Dvir
   *
   */
  public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
		private final IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}
  
  
  
  /**
   * This Mapper gets a <Ngram$DocName, sum of appearances> and returns <Ngram, sum of appearances $ DocName>
   * @author Dvir
   *
   */
  public static class MoveFileNameMapper extends Mapper<Text, Text, Text, Text> {
		private final Text word = new Text();
		private final Text docNameAndValue = new Text();
		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] splittedKey = key.toString().split("[$]");
			word.set(splittedKey[0]);
			docNameAndValue.set(value.toString()+"$"+splittedKey[1]);
			context.write(word, docNameAndValue );
		}
	}
  
  /**
   * This reducer gets a Ngram and list of all the documents which holds this Ngram and the number of apearnces of this Ngram in the documet
   * The mapper will return <DocName1$DocName2,total sum of Ngram apearnces in both docs> iff DocName1<DocName2
   * @author Dvir
   *
   */
  public static class FilesListReducer extends Reducer<Text,Text,Text,IntWritable > {
	  	//private final Text wordDocNameKey = new Text();
		private  Text docs = new Text();
		private IntWritable result = new IntWritable();
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			
			for(Text currentText: values)
			{
				String[] splittedCurrentDoc = currentText.toString().split("[$]");
				Integer currentValue =Integer.parseInt( splittedCurrentDoc[0]);
				String currentDocName = splittedCurrentDoc[1];
				map.put(currentDocName, currentValue);
			}
			
			for(String currentDocName : map.keySet())
			{
				for(String otherDocName : map.keySet())
				{
					if (currentDocName.compareTo(otherDocName)<0)
					{
						Integer sum = map.get(currentDocName)+map.get(otherDocName);
						
						result.set(sum);
						docs.set(currentDocName+"$"+otherDocName);
						context.write(docs, result);
					}
				}
			}
		}
	}
  
  /**
   * This Mapper gets <DocName1&DocName2, total sum of apearnces of Ngram in both documents> and 
   * return <DocName1 DocName2,total sum of apearnces of Ngram in both documents>
   * @author Dvir
   *
   */
  public static class FilesSumMapper extends Mapper<Text, Text, Text, Text> {
		private final Text docs = new Text();
		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			int n =  context.getConfiguration().getInt("n", 1);
			String[] splittedKey = key.toString().split("[$]");
			String doc1 = splittedKey[0];
			String doc2 = splittedKey[1];
			docs.set(doc1+ " " + doc2);
			context.write(docs, value);
		}
	}

  /**
   * This reducre gets <DocName1 DocName2,total sum of apearnces of Ngram in both documents> and returns
   * DocName1 DocName2,total sum of apearnces of all common Ngrams in both documents> iff this sum >=k
   * @author Dvir
   *
   */
  public static class IntKSumReducer extends Reducer<Text,Text,Text,IntWritable> {
		private final IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (Text val : values) {
				sum += Integer.parseInt(val.toString());
			}
			int k = context.getConfiguration().getInt("k", 0);
			if (sum >= k )
			{
				result.set(sum);
				context.write(key, result);
			}
			
		}
	}
  
  
  
  public static void main(String[] args) throws Exception 
  {
	  Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		// Just to be safe: clean temporary files before we begin
		fs.delete(TEMP_PATH, true);
		fs.delete(TEMP_PATH2, true);
		fs.delete(new Path(args[3]), true);
		conf.setInt("n", Integer.parseInt(args[0]));
		conf.setInt("k", Integer.parseInt(args[1]));
		
		

		/* We chain the three Mapreduce phases using a temporary directore
		from which the first phase writes to, and the second reads from */
		
		// Setup first MapReduce phase
		Job job1 = Job.getInstance(conf, "Detector-first");
		//conf.setInputFormat(WholeFileInputFormat.class);
		job1.setInputFormatClass(WholeFileInputFormat.class);
		job1.setJarByClass(Detector.class);
		job1.setMapperClass(NGramMapper.class);
		job1.setReducerClass(IntSumReducer.class);
		job1.setMapOutputKeyClass(Text.class);
		job1.setMapOutputValueClass(IntWritable.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job1, new Path(args[2]));
		FileOutputFormat.setOutputPath(job1, TEMP_PATH);
		//FileOutputFormat.setOutputPath(job1,  new Path(args[3]));
		boolean status1 = job1.waitForCompletion(true);
		if(!status1) {
			System.exit(1);
		}

		//Setup second MapReduce phase
		Job job2 = Job.getInstance(conf, "Detector-second");
		job2.setJarByClass(Detector.class);
		job2.setMapperClass(MoveFileNameMapper.class);
		job2.setReducerClass(FilesListReducer.class);
		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(Text.class);
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(IntWritable.class);
		job2.setInputFormatClass(KeyValueTextInputFormat.class);
		FileInputFormat.addInputPath(job2, TEMP_PATH);
		FileOutputFormat.setOutputPath(job2, TEMP_PATH2);
		//FileOutputFormat.setOutputPath(job2, new Path(args[3]));
		boolean status2 = job2.waitForCompletion(true);
		if (!status2) System.exit(1);
		// Clean temporary files from the first MapReduce phase
	//	fs.delete(TEMP_PATH, true);
		

		
		//Setup third MapReduce phase
		Job job3 = Job.getInstance(conf, "Detector-third");
		job3.setJarByClass(Detector.class);
		job3.setMapperClass(FilesSumMapper.class);
		job3.setReducerClass(IntKSumReducer.class);
		job3.setMapOutputKeyClass(Text.class);
		job3.setMapOutputValueClass(Text.class);
		job3.setOutputKeyClass(Text.class);
		job3.setOutputValueClass(IntWritable.class);
		job3.setInputFormatClass(KeyValueTextInputFormat.class);
		FileInputFormat.addInputPath(job3, TEMP_PATH2);
		FileOutputFormat.setOutputPath(job3, new Path(args[3]));
				
		boolean status3 = job3.waitForCompletion(true);
		if (!status3) System.exit(1);
		
		fs.delete(TEMP_PATH2, true);

  }
}
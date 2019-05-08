import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MyInvertedIndex {
	
	public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text>{
		
		// 단어를 Key로 저장하기 위한 Text 변수
		private Text keyInfo = new Text();
		// 문서ID:1 을 Value로 저장하기 위한 Text 변수
		private Text valueInfo = new Text();
		// 문서에 대한 정보들(파일 위치, 파일 이름 등)에 접근하기 위한 변수
		private FileSplit fs;
		
		public void map(Object key, Text value, Context context) 
				throws IOException, InterruptedException{
			
			// fs변수를 context를 통해 불러온다.
			fs = (FileSplit)context.getInputSplit();
			
			StringTokenizer st = new StringTokenizer(value.toString());
			// StringTokenizer(String, 구분자)로 한 줄을 분리함
            // 구분자가 없는 경우에는 모든 공백을 기준으로 입력을 분리
            // 공백을 기준으로 분리된 토큰을이 st 변수안에 저장되어 있음
            // ex) line = hello hadoop world 라고 하면, st에는[hello, hadoop, world]가 들어가있고
            // st.nextToken()할때마다 토큰을 st에서 하나씩 꺼냄
			
			while(st.hasMoreTokens()) {
				// 키에는 단어를 저장한다.
				keyInfo.set(st.nextToken());
				// 값으로는 문서이름:1 을 저장한다.
				valueInfo.set(fs.getPath().getName()+":"+1);
				// 단어와 해당 단어가 존재하는 문서이름:1 을 기록한다.
				context.write(keyInfo, valueInfo);
			}
		}
	}
	
	public static class InvertedIndexCombiner extends Reducer <Text, Text, Text, Text>{
		
		// 단어의 개수를 합치고, 그 값을 저장하기 위한 Text 변수
		private Text valueInfo = new Text();
		
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException{

			// 단어의 총 개수를 저장하기 위한 sum 변수
			int sum = 0;
			
			// 반복문을 돌면서 문장을 : 기준으로 split하여 개수를 더해준다.
			for(Text value : values) {
				// 문서이름:개수 를 ":"를 기준으로 분리한다.
				String[] strArr = value.toString().split(":");
				// 개수를 Integer로 변형하여 sum에 더해준다.
				sum += Integer.parseInt(strArr[1]);
				// 더해진 sum 값을 다시 문서이름:sum 으로 합하여 valueInfo 변수에 저장한다.
				valueInfo.set(String.valueOf(strArr[0]) + ":" + sum);
			}
			// 단어와 해당 단어가 존재하는 문서이름:sum 을 기록한다.
			context.write(key, valueInfo);
		}
	}
	
	public static class InvertedIndexReducer extends Reducer <Text, Text, Text, Text>{
		// 단어가 등장했던 문서이름:개수 를 하나의 String으로 합하여 저장하기 위한 Text 변수
		private Text valueInfo = new Text();
		
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException {
			
			// StringBuilder는 하나의 String을 만들기 위한 도구로 append를 통해 
			//내부에 데이터를 쌓고 마지막에 toString 메소드를 호출하여 하나의 스트링으로 변환한다.
			StringBuilder sb = new StringBuilder();
			
			// 하나의 단어에 대해 나온 문서들의 목록을 합쳐서 저장한다.
			for(Text value : values) {
				sb.append(value.toString()).append(",");
			}
			
			// 해당 단어가 나온 문서 목록을 하나의 스트링으로 만들어 저장하기 위해 valueInfo에 set해준다.
			valueInfo.set(sb.toString());
			
			//해당 단어, 단어가 나온 목록을 기록한다.
			context.write(key, valueInfo);
		}
	}
	
	public static void main(String[] args) throws Exception {

		// Configuration : 환경설정을 위한 클래스로 파일시스템의 위치를 설정
        Configuration conf = new Configuration();
        
        // Job : MapReduce의 Job
        Job job = Job.getInstance(conf, "Inverted Index");

        // 작성한 MyInvertedClass를 job으로, InvertedIndexMapper를 Mapper로, 
        // InvertedIndexCombiner를 Combiner로, InvertedIndexReducer를 Reducer로 설정
        job.setJarByClass(MyInvertedIndex.class);
        
        job.setMapperClass(InvertedIndexMapper.class);
        job.setCombinerClass(InvertedIndexCombiner.class);
        job.setReducerClass(InvertedIndexReducer.class);

        // Map 함수와 reduce 함수의 출력의 key, value를 설정
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // 입력파일이 분할되어 분할된 블록을 읽어들이는 방식
        job.setInputFormatClass(TextInputFormat.class);

        // HDFS에 결과를 저장하기 위한 방식
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  // 입력폴더
        FileOutputFormat.setOutputPath(job, new Path(args[1]));  // 출력폴더

        job.waitForCompletion(true);
		
	}
}


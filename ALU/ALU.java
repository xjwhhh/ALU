package cn.edu.nju.software151250171;
/**
 * 模拟ALU进行整数和浮点数的四则运算
 * @author [“151250171_徐佳炜”]
 *
 */

public class ALU {

	/**
	 * 生成十进制整数的二进制补码表示。<br/>
	 * 例：integerRepresentation("9", 8)
	 * @param number 十进制整数。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param length 二进制补码表示的长度
	 * @return number的二进制补码表示，长度为length
	 */
	public String integerRepresentation (String number, int length) {
		// TODO YOUR CODE HERE.
		String str=new String();
	    int a=Integer.parseInt(number);
	    int b=Math.abs(a);//绝对值
	    //整数至二进制的一种算法
	    while(b!=0){
            str= (b%2)+str;
            b=b/2;
        }
	    //正数,添符号位
	    if(a>=0){
	    	for(int i=str.length();i<length;i++){
	    		str="0"+str;
	    	}
	    }
	    //负数，取反加一，添符号位
	    else{
	    	str=this.negation(str);
	    	str=this.oneAdder(str);
	    	str=str.substring(1,str.length());
	    	for(int i=str.length();i<length;i++){
	    		str="1"+str;
	    	}
	    }		
		return str;
	}
		/**
	 * 生成十进制浮点数的二进制表示。
	 * 需要考虑 0、反规格化、正负无穷（“+Inf”和“-Inf”）、 NaN等因素，具体借鉴 IEEE 754。
	 * 舍入策略为向0舍入。<br/>
	 * 例：floatRepresentation("11.375", 8, 11)
	 * @param number 十进制浮点数，包含小数点。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return number的二进制表示，长度为 1+eLength+sLength。从左向右，依次为符号、指数（移码表示）、尾数（首位隐藏）
	 */
	public String floatRepresentation (String number, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
		String str=new String();
		String zhengshu=new String();
		String xiaoshu=new String();
		String fraction=new String();
		String exponent=new String();
		//0的情况
		if(number=="0"){
			for(int i=0;i<1+eLength+sLength;i++){
				str="0"+str;
			}
		}
		int u=0;
		char[] newnumber=number.toCharArray();
		for(int m=0;m<newnumber.length;m++){
			if(newnumber[m]=='.'){
				u=u+1;
			}
		}
		if(u!=0){
		String[] yuanshuzhi=number.split("[.]");
		//整数与小数的十进制表示
		int zs=0;
		if(number.charAt(0)!='-'){
			zs=Integer.parseInt(yuanshuzhi[0]);
		}
		else{
			zs=Integer.parseInt(String.valueOf(yuanshuzhi[0]).substring(1));
		}
		Double xs=Double.parseDouble("0."+yuanshuzhi[1]);
		//整数部分的二进制原码表示
		while(zs!=0){
			zhengshu=(zs%2)+zhengshu;
			zs=zs/2;
		}
		//小数部分的二进制表示
		while(xs!=0){
			xs=xs*2;
			if(xs>=1){
				xiaoshu=xiaoshu+"1";
				xs=xs-1;
			}
			else{
				xiaoshu=xiaoshu+"0";
			}
		}
		fraction=zhengshu+xiaoshu;
		String newfra=new String();
		int ex=0;
		ex=-xiaoshu.length();
		char[] fra=fraction.toCharArray();
		for(int i=0;i<fra.length;i++){
			if(fra[i]=='1'){
				for(int j=i+1;j<fra.length;j++){
					newfra=newfra+String.valueOf(fra[j]);
					ex=ex+1;
				}
				break;
			}
		}
		//指数
		ex=ex+127;
		int ex1=ex;
		//尾数
		while(newfra.length()<sLength){
			newfra=newfra+"0";
		}
		//反规格化数
		if(ex<-Math.pow(2,eLength-1)+1){
			newfra="1"+newfra.substring(0,newfra.length()-1);
			ex=ex+1;
		}
		while(ex<-Math.pow(2,eLength-1)+1){
			newfra=logRightShift(newfra,1);
			ex=ex+1;
		}
		//规格化数
		 while(ex!=0){
			exponent=(ex%2)+exponent;
			ex=ex/2;
		}
		while(exponent.length()<eLength){
			exponent="0"+exponent;
		}
		str=exponent+newfra;
		
		//添加符号位
				if(number.charAt(0)=='-'){
					str="1"+str;
				}
				else{
					str="0"+str;
				}
				//无穷
		if(ex1>Math.pow(2,eLength)-1){
			if(number.charAt(0)=='-'){
				str="+Inf";
			}
			else{
				str="-Inf";
			}
			}
		}
		return str;
	}
	
	/**
	 * 生成十进制浮点数的IEEE 754表示，要求调用{@link #floatRepresentation(String, int, int) floatRepresentation}实现。<br/>
	 * 例：ieee754("11.375", 32)
	 * @param number 十进制浮点数，包含小数点。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param length 二进制表示的长度，为32或64
	 * @return number的IEEE 754表示，长度为length。从左向右，依次为符号、指数（移码表示）、尾数（首位隐藏）
	 */
	public String ieee754 (String number, int length) {
		// TODO YOUR CODE HERE.
		String str = new String();
		if(length == 32 ){
			str = floatRepresentation(number,8,23);
		}
		else{
			str = floatRepresentation(number,11,52);
		}
		return str;
	}
	
	/**
	 * 计算二进制补码表示的整数的真值。<br/>
	 * 例：integerTrueValue("00001001")
	 * @param operand 二进制补码表示的操作数
	 * @return operand的真值。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 */
	public String integerTrueValue (String operand) {
		// TODO YOUR CODE HERE.
		char[] ch1=operand.toCharArray();
		double number=0;
		String str=null;
		if(Integer.parseInt(String.valueOf(ch1[0]))==0){//检验正负
			for(int i=1;i<ch1.length;i++){
					int a=Integer.parseInt(String.valueOf(ch1[i]));
				number=number+a*Math.pow(2,ch1.length-i-1);
			}
			int number1=(int)number;
			str=Integer.toString(number1);
		}
		else{
			number=(Math.pow(2,ch1.length-1));
			for(int i=1;i<ch1.length;i++){
				int a=Integer.parseInt(String.valueOf(ch1[i]));
				number=number-a*Math.pow(2,ch1.length-i-1);
			}
			int  number1=(int)number;
			str=String.valueOf(number1);
			char[] ch2=str.toCharArray();
			char[] ch3=new char[ch2.length+1];
			ch3[0]='-';
			for(int j=1;j<ch2.length+1;j++){
				ch3[j]=ch2[j-1];
			}
			str=String.valueOf(ch3);
		}
		return str;
	}
	
	/**
	 * 计算二进制原码表示的浮点数的真值。<br/>
	 * 例：floatTrueValue("01000001001101100000", 8, 11)
	 * @param operand 二进制表示的操作数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return operand的真值。若为负数；则第一位为“-”；若为正数或 0，则无符号位。正负无穷分别表示为“+Inf”和“-Inf”， NaN表示为“NaN”
	 */
	public String floatTrueValue (String operand, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
		boolean isnagetive = false;
		if(operand.charAt(0)=='1'){
			isnagetive = true;
		}
		String exponent = operand.substring(1,eLength+1);
		String fraction = operand.substring(eLength+1);
		String ex0=new String();
		String ex1=new String();
		String fr =new String();
		for(int i=eLength;i>=1;i--){
			ex0=ex0+"0";
		}
		for(int i=eLength;i>=1;i--){
			ex1=ex1+"1";
		}
		for(int i=sLength;i>=1;i--){
			fr=fr+"0";
		}
		//0
		if(exponent.equalsIgnoreCase(ex0)&&fraction.equalsIgnoreCase(fr)){
			return "0";
		}
		//无穷
		if(exponent.equalsIgnoreCase(ex1)&&fraction.equalsIgnoreCase(fr)){
			if(isnagetive){
				return "-Inf";
			}
			else{
				return "+Inf";
		}
		}
		//非数
		if(exponent.equalsIgnoreCase(ex1)&&(!fraction.equalsIgnoreCase(fr))){
			return "NaN";
		}
		double result=0;
		//指数真值
		int e1 = 0;
		for(int i=eLength-1;i>=0;i--){
			if(exponent.charAt(i)=='1'){
				e1 = e1 + (int)Math.pow(2,eLength-1-i);
		}
		}
		e1 = e1 - (int)(Math.pow(2,eLength-1)-1);
		//反规格化
		if(exponent.equalsIgnoreCase(ex0)&&!fraction.equalsIgnoreCase(fr)){
			for(int i=0;i<=fraction.length()-1;i++){
				if(fraction.charAt(i)=='1'){
					result = result + Math.pow(2,-(i+1));
			}
			}
			result = result * Math.pow(2,e1+1);
			if(isnagetive){
				return "-"+result;
			}
			else{
				return result+"";
		}
		}
		//规格化
		if(e1>=1){//要分成整数和小数
			String part1;//整数
			String part2;//小数
			part1="1"+fraction.substring(0,e1);
			part2=fraction.substring(e1);
			for(int i=part1.length()-1;i>=0;i--){
				if(part1.charAt(i)=='1'){
					result = result + Math.pow(2,part1.length()-1-i);
			}
			}
			for(int i=0;i<=part2.length()-1;i++){
				if(part2.charAt(i)=='1'){
					result = result + Math.pow(2,-(i+1));
			}
			}
		}
		else{
			for(int i=0;i<=fraction.length()-1;i++){
				if(fraction.charAt(i)=='1'){
					result = result + Math.pow(2,-(i+1));
			}
			}
			result = result + 1;
			result = result * Math.pow(2,e1);
		}
		if(isnagetive){
			return "-"+result;
		}
		else{
			return result+"";
	}
	}
	
	
	/**
	 * 按位取反操作。<br/>
	 * 例：negation("00001001")
	 * @param operand 二进制表示的操作数
	 * @return operand按位取反的结果
	 */
	public String negation (String operand) {
		// TODO YOUR CODE HERE.
		char [] ch=operand.toCharArray();
		for(int j=0;j<ch.length;j++){
			if(ch[j]=='0'){
				ch[j]='1';
			}
			else{
				ch[j]='0';
			}
		}
		String str=String.valueOf(ch);
		return str;
	}
	/**
	 * 左移操作。<br/>
	 * 例：leftShift("00001001", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 左移的位数
	 * @return operand左移n位的结果
	 */
	public String leftShift (String operand, int n) {
		// TODO YOUR CODE HERE.
		char[] ch1=operand.toCharArray();
		char[] ch2=new char[ch1.length];
		for(int i=0;i<ch1.length-n;i++){
			ch2[i]=ch1[i+n];
		}
		for(int j=ch1.length-n;j<ch1.length;j++){
			ch2[j]='0';//左移加0
		}
		String str=String.valueOf(ch2);
		return str;
	}
	
	/**
	 * 逻辑右移操作。<br/>
	 * 例：logRightShift("11110110", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 右移的位数
	 * @return operand逻辑右移n位的结果
	 */
	public String logRightShift (String operand, int n) {
		// TODO YOUR CODE HERE.
		char[] ch1=operand.toCharArray();
		char[] ch2=new char[ch1.length];
		for(int i=0;i<n;i++){
			ch2[i]='0';//逻辑右移加0
		}
		for(int j=n;j<ch1.length;j++){
			ch2[j]=ch1[j-n];
		}
		String str=String.valueOf(ch2);
		return str;
	}
	
	/**
	 * 算术右移操作。<br/>
	 * 例：logRightShift("11110110", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 右移的位数
	 * @return operand算术右移n位的结果
	 */
	public String ariRightShift (String operand, int n) {
		// TODO YOUR CODE HERE.
		char[] ch1=operand.toCharArray();
		char[] ch2=new char[ch1.length];
		for(int j=n;j<ch1.length;j++){
			ch2[j]=ch1[j-n];
		}
		for(int i=0;i<n;i++){
			if(ch1[0]=='0'){
				ch2[i]='0';
			}
			else{
				ch2[i]='1';//算术右移加符号位
			}
		}
		String str=String.valueOf(ch2);
		return str;
	}
	
	/**
	 * 全加器，对两位以及进位进行加法运算。<br/>
	 * 例：fullAdder('1', '1', '0')
	 * @param x 被加数的某一位，取0或1
	 * @param y 加数的某一位，取0或1
	 * @param c 低位对当前位的进位，取0或1
	 * @return 相加的结果，用长度为2的字符串表示，第1位表示进位，第2位表示和
	 */
	public String fullAdder (char x, char y, char c) {
		// TODO YOUR CODE HERE.
		int sum=Integer.parseInt(String.valueOf(x))+Integer.parseInt(String.valueOf(y))+Integer.parseInt(String.valueOf(c));
		char[] ch=new char[2];
		//枚举四种情况
		if(sum==0){
			ch[0]='0';
			ch[1]='0';
		}
		else if(sum==1){
			ch[0]='0';
			ch[1]='1';
		}
		else if(sum==2){
			ch[0]='1';
			ch[1]='0';
		}
		else if(sum==3){
			ch[0]='1';
			ch[1]='1';
		}
		String str=String.valueOf(ch);	
		return str;
	}
	
	/**
	 * 4位先行进位加法器。要求采用{@link #fullAdder(char, char, char) fullAdder}来实现<br/>
	 * 例：claAdder("1001", "0001", '1')
	 * @param operand1 4位二进制表示的被加数
	 * @param operand2 4位二进制表示的加数
	 * @param c 低位对当前位的进位，取0或1
	 * @return 长度为5的字符串表示的计算结果，其中第1位是最高位进位，后4位是相加结果，其中进位不可以由循环获得
	 */
	public String claAdder (String operand1, String operand2, char c) {
		// TODO YOUR CODE HERE.
		char[] ch2=operand2.toCharArray();
		char[] ch1=operand1.toCharArray();
		char[] ch=new char[2];
		String str=new String();
		for(int i=3;i>=0;i--){
		String str1=this.fullAdder(ch1[i],ch2[i], c);
			ch=str1.toCharArray();
			c=ch[0];
			str=String.valueOf(ch[1])+str;
		}//计算四次
		str=String.valueOf(ch[0])+str;//加上进位
		return str;
		
	}
	
	/**
	 * 加一器，实现操作数加1的运算。
	 * 需要采用与门、或门、异或门等模拟，
	 * 不可以直接调用{@link #fullAdder(char, char, char) fullAdder}、
	 * {@link #claAdder(String, String, char) claAdder}、
	 * {@link #adder(String, String, char, int) adder}、
	 * {@link #integerAddition(String, String, int) integerAddition}方法。<br/>
	 * 例：oneAdder("00001001")
	 * @param operand 二进制补码表示的操作数
	 * @return operand加1的结果，长度为operand的长度加1，其中第1位指示是否溢出（溢出为1，否则为0），其余位为相加结果
	 */
	public String oneAdder (String operand) {
		// TODO YOUR CODE HERE.
		char[] ch=operand.toCharArray();
		String str=new String();
		//溢出的可能性：01111111~+1=100000~
		String str1=new String();
		for(int i=1;i<ch.length;i++){
			str1="1"+str1;
		}
		str1="0"+str1;
		if(operand.equals(str1)){
			ch[0]='1';
			for(int j=1;j<ch.length;j++){
				ch[j]='0';
			}
			str="1"+String.valueOf(ch);
		}//只有这种可能溢出
		else{
			for(int k=ch.length-1;k>=0;k--){
				if(ch[k]=='1'){
					ch[k]='0';
				}
				else{
					ch[k]='1';
					break;
				}
			}
			str="0"+String.valueOf(ch);
		}
		return str;
	}
	
	/**
	 * 加法器，要求调用{@link #claAdder(String, String, char)}方法实现。<br/>
	 * 例：adder("0100", "0011", ‘0’, 8)
	 * @param operand1 二进制补码表示的被加数
	 * @param operand2 二进制补码表示的加数
	 * @param c 最低位进位
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相加结果
	 */
	public String adder (String operand1, String operand2, char c, int length) {
		// TODO YOUR CODE HERE.
		//符号扩展
		char[] ch1=operand1.toCharArray();
		char[] ch2=operand2.toCharArray();
		String str1=new String();
		String str2=new String();
		String str=new String();
		if(ch1.length<length){
			for(int i=0;i<length-ch1.length;i++){
				str1=String.valueOf(ch1[0])+str1;
			}	
		}
		str1=str1+operand1;
		if(ch2.length<length){
			for(int i=0;i<length-ch2.length;i++){
				str2=String.valueOf(ch2[0])+str2;
			}
		}
		str2=str2+operand2;
	//分成每四位一组，进行四位先行加法
		int a=length/4;//a个组
		for(int i=a;i>=1;i--){
		String str11=str1.substring(4*i-4,4*i);
		String str22=str2.substring(4*i-4,4*i);
		String str33=this.claAdder(str11,str22, c);
		char[] ch3=str33.toCharArray();
		c=ch3[0];
		str=str33.substring(1,5)+str;
		}
		//得到了length长度的str，接下来判断是否溢出
		//调用IntegerTrueValue,判断是否在表示范围内
		String s1=this.integerTrueValue(str1);
		String s2=this.integerTrueValue(str2);
		int m=Integer.parseInt(s1);
		int n=Integer.parseInt(s2);
		if((m+n)>=Math.pow(2,length-1)-1||(m+n)<-Math.pow(2,length-1)){
			str="1"+str;
		}
		else{
			str="0"+str;
		}
				return str;
	}
	
	/**
	 * 整数加法，要求调用{@link #adder(String, String, char, int) adder}方法实现。<br/>
	 * 例：integerAddition("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被加数
	 * @param operand2 二进制补码表示的加数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相加结果
	 */
	public String integerAddition (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		String str=this.adder(operand1,operand2,'0',length);
		return str;
	}
	
	/**
	 * 整数减法，可调用{@link #adder(String, String, char, int) adder}方法实现。<br/>
	 * 例：integerSubtraction("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被减数
	 * @param operand2 二进制补码表示的减数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相减结果
	 */
	public String integerSubtraction (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		String str=adder(operand1,negation(operand2),'1',length);
		return str;
		
	}
	
	/**
	 * 整数乘法，使用Booth算法实现，可调用{@link #adder(String, String, char, int) adder}等方法。<br/>
	 * 例：integerMultiplication("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被乘数
	 * @param operand2 二进制补码表示的乘数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的相乘结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相乘结果
	 */
	public String integerMultiplication (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		//符号位扩展
		while(operand1.length()<length){
			operand1 = operand1.charAt(0)+operand1;
			}
		while(operand2.length()<length){
			operand2 = operand2.charAt(0)+operand2;
			}
		String str1 = operand2 + "0";//乘数末尾加0
		String str2 =new String() ;
		for(int i=length;i>=1;i--)
			str2 = str2 + "0";//A寄存器初始化
		String str3 = operand2;//Q寄存器初始化
		String str = str2 + str3;
		for(int i=length;i>=1;i--){//进行length次
			int temp = str1.charAt(i)-str1.charAt(i-1);
			str2 = str.substring(0,length);
			str3 = str.substring(length);
			if(temp==1){
				str2 =adder(str2,operand1,'0',length).substring(1);//0-1 加
			}else if(temp==-1){
				str2 = integerSubtraction(str2,operand1,length).substring(1);//1-0减
			}
			str = str2+str3;
			str = ariRightShift(str,1);//算术右移
		}
		str=str.substring(length);
		//判断溢出
		long a=Long.parseLong(integerTrueValue(str));
		long b=Long.parseLong(integerTrueValue(operand2))*Integer.parseInt(integerTrueValue(operand1));
		
		if(a==b){
			str="0"+str;
		}
		else{
			str="1"+str;
		}
		return str;
	}
	
	/**
	 * 整数的不恢复余数除法，可调用{@link #adder(String, String, char, int) adder}等方法实现。<br/>
	 * 例：integerDivision("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被除数
	 * @param operand2 二进制补码表示的除数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为2*length+1的字符串表示的相除结果，其中第1位指示是否溢出（溢出为1，否则为0），其后length位为商，最后length位为余数
	 */
	public String integerDivision (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		//符号位扩展
		int x=0;
		while(operand1.length()<length){
			operand1 = operand1.charAt(0)+operand1;
			}
		while(operand2.length()<length){
			operand2 = operand2.charAt(0)+operand2;
			}
		String remainder=new String();
		for(int i=0;i<length;i++){
			remainder=remainder+operand1.charAt(0);
		}//余数寄存器初始化
		String quotient=operand1;//商寄存器初始化
		String dividend=remainder+quotient;//被除数初始化
		String divisor=operand2;//除数初始化
		//第一次加减
		if(dividend.charAt(0)==divisor.charAt(0)){
			remainder=integerSubtraction(remainder,divisor, length).substring(1);
		}
		else{
			remainder=integerAddition(remainder,divisor, length).substring(1);
		}//如果被除数和除数有相同符号，余数减除数，否则加
		dividend=remainder+quotient;
		if(remainder.charAt(0)==divisor.charAt(0)){
			x=1;
		}
		else{
			x=0;
		}
		//length次移位+加减
		for(int i=0;i<length;i++){
			if(remainder.charAt(0)==divisor.charAt(0)){
				dividend=dividend.substring(1)+"1";
			}
			else{
				dividend=dividend.substring(1)+"0";
			}//如果余数与除数同号，商最后一位准备上1，否则上0,左移，商最后一位添上；
			remainder=dividend.substring(0,length);
			quotient=dividend.substring(length);
			if(dividend.charAt(0)==divisor.charAt(0)){
				remainder=integerSubtraction(remainder,divisor, length).substring(1);
			}
			else{
				remainder=integerAddition(remainder,divisor, length).substring(1);
			}//如果被除数和除数有相同符号，余数减除数，否则加
			dividend=remainder+quotient;
			}
		//修正
		if(remainder.charAt(0)==divisor.charAt(0)){
			quotient=quotient.substring(1)+"1";
		}
		else{
			quotient=quotient.substring(1)+"0";
		}//根据余数与除数的关系，左移并添上商最后一位
		if(operand1.charAt(0)!=operand2.charAt(0)){
			quotient=oneAdder(quotient).substring(1);
		}//如果被除数与除数异号，商要加上一
		if(remainder.charAt(0)!=operand1.charAt(0)){
			if(operand1.charAt(0)!=operand2.charAt(0)){
				remainder=integerSubtraction(remainder,divisor, length).substring(1);
			}
			else{
				remainder=integerAddition(remainder,divisor, length).substring(1);
			}
		}//如果余数与被除数异号：如果被除数与除数同号，余数加除数；否则，余数减除数
		//判断溢出
		String result=new String();
		if((operand1.charAt(0)==operand2.charAt(0)&&x==1)&&(operand1.charAt(0)!=operand2.charAt(0)&&x==0)){
			result="1"+quotient+remainder;
		}
		else{
			result="0"+quotient+remainder;
		}
		return result;
	}
	
	/**
	 * 带符号整数加法，可以调用{@link #adder(String, String, char, int) adder}等方法，
	 * 但不能直接将操作数转换为补码后使用{@link #integerAddition(String, String, int) integerAddition}、
	 * {@link #integerSubtraction(String, String, int) integerSubtraction}来实现。<br/>
	 * 例：signedAddition("1100", "1011", 8)
	 * @param operand1 二进制原码表示的被加数，其中第1位为符号位
	 * @param operand2 二进制原码表示的加数，其中第1位为符号位
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度（不包含符号），当某个操作数的长度小于length时，需要将其长度扩展到length
	 * @return 长度为length+2的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），第2位为符号位，后length位是相加结果
	 */
	public String signedAddition (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		//符号位扩展
		String str1=operand1.substring(1);
		String str2=operand2.substring(1);
		String str=new String();
		while(str1.length()<length-1){
			str1="0"+str1;
		}
			str1=operand1.substring(0,1)+str1;
		while(str2.length()<length-1){
			str2="0"+str2;
		}
			str2=operand2.substring(0,1)+str2;
		//判断符号位
			//同号,有可能溢出
		if(str1.charAt(0)==str2.charAt(0)){
			str=this.adder(str1.substring(1), str2.substring(1),'0', length).substring(1);
			char[] ch1=str1.toCharArray();
			char[] ch2=str2.toCharArray();
			double x=0;
			double y=0;
			for(int i=1;i<length;i++ ){
				x=Integer.parseInt(String.valueOf(ch1[i]))*Math.pow(2,length-1-i)+x;
				y=Integer.parseInt(String.valueOf(ch2[i]))*Math.pow(2,length-1-i)+y;
			}
			double num=x+y;
				if(num>=Math.pow(2,length)-1
						){
					str="1"+str1.charAt(0)+str;
				}
				else{
					str="0"+str1.charAt(0)+str;
				}
			}
		//异号，不可能溢出
		if(str1.charAt(0)!=str2.charAt(0)){
			//大数减小数，化为原码
			char[] ch1=str1.toCharArray();
			char[] ch2=str2.toCharArray();
			double x=0;
			double y=0;
			for(int i=1;i<length;i++ ){
				x=Integer.parseInt(String.valueOf(ch1[i]))*Math.pow(2,length-1-i)+x;
				y=Integer.parseInt(String.valueOf(ch2[i]))*Math.pow(2,length-1-i)+y;
			}
			double num=Math.max(x,y)-Math.min(x,y);
			String temp =new String();
			int num1=(int)num;
			while(num1!=0){
				int m = num1%2;
				num1 = num1/2;
				temp = m+temp;
			}
			while(temp.length()<length){
				temp = "0"+temp;
			}
			//判断符号位
			if((str1.charAt(0)=='1'&&x>=y)||(str1.charAt(0)=='0'&&x<y)){
				str="0"+"1"+temp;
			}
			else{
				str="0"+"0"+temp;
			}
		}
		return str;
	}
	
	/**
	 * 浮点数加法，可调用{@link #signedAddition(String, String, int) signedAddition}等方法实现。<br/>
	 * 例：floatAddition("00111111010100000", "00111111001000000", 8, 8, 8)
	 * @param operand1 二进制表示的被加数
	 * @param operand2 二进制表示的加数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @param gLength 保护位的长度
	 * @return 长度为2+eLength+sLength的字符串表示的相加结果，其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	//写一个方法，由原码到十进制
	public int  gettruevalue (String operand) {
		double x=0;
		char[] op=operand.toCharArray();
		for(int i=0;i<op.length;i++){
			x=Integer.parseInt(String.valueOf(op[i]))*Math.pow(2,op.length-i-1)+x;
		}
		return (int)x;
	}
	public String floatAddition (String operand1, String operand2, int eLength, int sLength, int gLength) {
		// TODO YOUR CODE HERE.

		String exponent1=operand1.substring(1,eLength+1);
		String fraction1=operand1.substring(eLength+1);
		String sign1=operand1.substring(0,1);
		String exponent2=operand2.substring(1,eLength+1);
		String fraction2=operand2.substring(eLength+1);
		String sign2=operand2.substring(0,1);
		String str=new String();
		String zero=new String();
		for(int j=0;j<=sLength+gLength;j++){
			zero="0"+zero;
		}
		//尾数+保护位
		for(int i=0;i<gLength;i++){
			fraction1=fraction1+"0";
			fraction2=fraction2+"0";
		}
		fraction1="1"+fraction1;
		fraction2="1"+fraction2;
		//0操作数的检查
		if(floatTrueValue(operand1,sLength,eLength).equalsIgnoreCase("0")){
			str="0"+operand2;
		}
		else if(floatTrueValue(operand2,sLength,eLength).equalsIgnoreCase("0")){
			str="0"+operand1;
		}
		//指数真值
		int ev1=gettruevalue(exponent1);
		int ev2=gettruevalue(exponent2);
		int ev=ev1;//ev为对齐后的指数大小
		//比较指数大小并完成对齐
		if(ev1>ev2){
			fraction2=logRightShift(fraction2,ev1-ev2);
			//较小数丢失
			if(fraction2.equals(zero)){
				str="0"+operand1;
			}
		}
		else if(ev1<ev2){
			fraction1=logRightShift(fraction1,ev2-ev1);
			ev=ev2;
			//较小数丢失
			if(fraction1.equals(zero)){
				str="0"+operand2;
			}
		}
		//带符号尾数相加
		fraction1=sign1+fraction1;
		fraction2=sign2+fraction2;
		int k=0;
		while((4*k)<fraction1.length()){
			k++;
		}
		String fraction=signedAddition(fraction1, fraction2,4*k);//长度为4*k+2
		String sign=fraction.substring(1,2);//获得符号位

		fraction=fraction.substring(4*k-fraction1.length()+2);
		//判断有效数是否上溢,如果溢出，有效数右移，指数增加一
		boolean weishuyichu=false;
		double x=0;
		double y=0;
		char[] f1=fraction1.toCharArray();
		char[] f2=fraction2.toCharArray();
		for(int i=0;i<fraction1.length();i++){
			x=Integer.parseInt(String.valueOf(f1[i]))*Math.pow(2,f1.length-1-i)+x;
			y=Integer.parseInt(String.valueOf(f2[i]))*Math.pow(2,f2.length-1-i)+y;
		}
		if(x+y>Math.pow(2,sLength+gLength+1)-1){
			weishuyichu=true;
		}
			if(weishuyichu){
				fraction=logRightShift(fraction, 1);
				ev=ev+1;
			}
			
			boolean zhishuisyichu=false;
			//判断指数是否上溢
			if(ev>Math.pow(2,eLength-1)-1){
				zhishuisyichu=true;
			}
		//规格化结果，左移有效数，减少指数
			ev=ev+4*k-fraction1.length();
			fraction=fraction.substring(0,fraction.length()-4);
		while(fraction.charAt(0)=='0'){
			fraction=leftShift(fraction, 1);
			ev=ev-1;
		}
		fraction=leftShift(fraction, 1);
		ev=ev-1;
		fraction=fraction.substring(0,sLength);
		String exponent=new String();
		while(ev!=0){
			int m = ev%2;
			ev = ev/2;
			exponent = m+exponent;
		}
		while(exponent.length()<eLength){
			exponent="0"+exponent;
		}
		if(zhishuisyichu){
			str="1"+sign+exponent+fraction;
		}
		else{
			str="0"+sign+exponent+fraction;
		}
		return str;
	}
	/**
	 * 浮点数减法，可调用{@link #floatAddition(String, String, int, int, int) floatAddition}方法实现。<br/>
	 * 例：floatSubtraction("00111111010100000", "00111111001000000", 8, 8, 8)
	 * @param operand1 二进制表示的被减数
	 * @param operand2 二进制表示的减数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @param gLength 保护位的长度
	 * @return 长度为2+eLength+sLength的字符串表示的相减结果，其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatSubtraction (String operand1, String operand2, int eLength, int sLength, int gLength) {
		// TODO YOUR CODE HERE.
		if(operand2.charAt(0)=='0'){
			operand2="1"+operand2.substring(1);
		}
		else{
			operand2="0"+operand2.substring(1);
		}
		String str=floatAddition(operand1, operand2, eLength, sLength, gLength);
		return str;
	}
	
	/**
	 * 浮点数乘法，可调用{@link #integerMultiplication(String, String, int) integerMultiplication}等方法实现。<br/>
	 * 例：floatMultiplication("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 二进制表示的被乘数
	 * @param operand2 二进制表示的乘数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return 长度为2+eLength+sLength的字符串表示的相乘结果,其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatMultiplication (String operand1, String operand2, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
		String exponent1=operand1.substring(1,eLength+1);
		String fraction1=operand1.substring(eLength+1);
		String sign1=operand1.substring(0,1);
		String exponent2=operand2.substring(1,eLength+1);
		String fraction2=operand2.substring(eLength+1);
		String sign2=operand2.substring(0,1);
		String str=new String();
		boolean isyichu=false;
		String exponent=new String();
		String fraction=new String();
		//构造比较用
		String ex=new String();
		String fra=new String();
		for(int i=0;i<eLength;i++){
			ex="0"+ex;
		}
		for(int j=0;j<sLength;j++){
			fra="0"+fra;
		}
		//考虑0的情况
		if((exponent1.equals(ex)&&fraction1.equals(fra))||(exponent2.equals(ex)&&fraction2.equals(fra))){
			for(int k=0;k<=eLength+sLength+1;k++){
				str="0"+str;
			}
		}
		//指数真值
		else{
		double exp=gettruevalue(exponent1)+gettruevalue(exponent2)-Math.pow(2,eLength-1)+1;
		
		if(exp>Math.pow(2,eLength-1)){
			isyichu=true;
		}
		
		//尾数相乘并舍入
		
		fraction1="01"+fraction1.substring(1);
		fraction2="01"+fraction2.substring(1);
		int k=0;
		while(4*k<2*fraction1.length()){
			k=k+1;
		}
		fraction=integerMultiplication(fraction1, fraction2,4*k);
		fraction=fraction.substring((4*k-2*fraction1.length()+1)*2);
		
		fraction=fraction.substring(0,sLength);
		//规格化
		if(exp<-Math.pow(2,eLength-1)+1){
			fraction=logRightShift(fraction,1);
			exp=exp+1;
		}
		int exp1=(int) exp;
		while(exp1!=0){
			exponent=(exp1%2)+exponent;
			exp1=exp1/2;
			
		}
		if(exponent.length()<eLength){
			exponent="0"+exponent;
		}
		exponent=exponent.substring(0,eLength);
		//符号位
		if(sign1.equals(sign2)){
			str="0"+exponent+fraction;
		}
		else{
			str="1"+exponent+fraction;
		}
		//是否溢出
		if(isyichu){
			str="1"+str;
		}
		else{
			str="0"+str;
		}
		}
			
			
		return str;
	}
	
	/**
	 * 浮点数除法，可调用{@link #integerDivision(String, String, int) integerDivision}等方法实现。<br/>
	 * 例：floatDivision("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 二进制表示的被除数
	 * @param operand2 二进制表示的除数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return 长度为2+eLength+sLength的字符串表示的相乘结果,其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatDivision (String operand1, String operand2, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
		String exponent1=operand1.substring(1,eLength+1);
		String fraction1=operand1.substring(eLength+1);
		String sign1=operand1.substring(0,1);
		String exponent2=operand2.substring(1,eLength+1);
		String fraction2=operand2.substring(eLength+1);
		String sign2=operand2.substring(0,1);
		String str=new String();
		boolean isyichu=false;
		String exponent=new String();
		String fraction=new String();
		//构造比较用
				String ex=new String();
				String fra=new String();
				for(int i=0;i<eLength;i++){
					ex="0"+ex;
				}
				for(int j=0;j<sLength;j++){
					fra="0"+fra;
				}
		//考虑除数为0
		if(exponent2.equals(ex)&&fraction2.equals(fra)){
			str="NaN";
		}
		//考虑被除数为0
		if(exponent1.equals(ex)&&fraction1.equals(fra)){
			for(int k=0;k<=eLength+sLength+1;k++){
				str="0"+str;
			}
		}
		//指数真值
		else{
		double exp=gettruevalue(exponent1)-gettruevalue(exponent2)+Math.pow(2,eLength-1)-1;
		
		if(exp>Math.pow(2,eLength-1)){
			isyichu=true;
		}
		//尾数相除并舍入
		fraction1="1"+fraction1;
		fraction2="1"+fraction2;
		String newfra="";
		for(int i=0;i<fraction1.length();i++){
			newfra=newfra+"0";
		}
		
		String frac=fraction1;
		fraction=frac+newfra;
		int k=0;
		while(4*k<fraction1.length()){
			k=k+1;
		}
		String newfra1="";
		for(int j=0;j<fraction1.length();j++){
			frac=fraction.substring(0,fraction1.length());
			newfra1=fraction.substring(fraction1.length());
			 int x=gettruevalue(frac)-gettruevalue(fraction2);
			 
			 if(x>=0){
				 frac=integerRepresentation(String.valueOf(Math.abs(x)),fraction1.length()-1);
				 frac="0"+frac;
			 }
			 fraction=frac+newfra1;
			if(x>=0){
				fraction=fraction.substring(1)+"1";
			}
			else{
				fraction=fraction.substring(1)+"0";
			}
		}
		fraction=fraction.substring(fraction1.length()+1);
		System.out.println(fraction);
		//规格化
		if(exp<-Math.pow(2,eLength-1)+1){
			fraction=logRightShift(fraction,1);
			exp=exp+1;
		}
		int exp1=(int) exp;
		while(exp1!=0){
			exponent=(exp1%2)+exponent;
			exp1=exp1/2;
			
		}
		if(exponent.length()<eLength){
			exponent="0"+exponent;
		}
		exponent=exponent.substring(0,eLength);
		System.out.println(exponent);
		//符号位
		if(sign1.equals(sign2)){
			str="0"+exponent+fraction;
		}
		else{
			str="1"+exponent+fraction;
		}
		//是否溢出
		if(isyichu){
			str="1"+str;
		}
		else{
			str="0"+str;
		}
		}
		return str;
	}
}


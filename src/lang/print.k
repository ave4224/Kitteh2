print(long input){
	inputCopy:=input
	int count=0
	if (long)(0) >inputCopy{
		inputCopy=0-inputCopy//make it positive so "for inputCopy > 0" works properly
		count=count+1//make room for the negative sign
	}
	for inputCopy > (long)0{
		inputCopy = inputCopy / 10
		count=count+1
	}
	if count == 0{//even if the input is just zero, the output needs to have the "0" char
		count=1
	}
	totalSize:=count+1
	result:=(byte*)malloc(count+1)
	result[count]=10
	count--
	inputCopy=input
	neg:=inputCopy<(long)0
	if neg{//if we are doing a negative number
		inputCopy=0-inputCopy//make it positive
	}
	for count>=0{
		dig:=(byte)(inputCopy%(long)10)//the digit as a byte from 0 to 9
		dig=dig+'0'//make it a real ascii character by adding '0' to it
		result[count]=dig//set it in the output
		inputCopy=inputCopy/10
		count=count-1
	}
	if neg{
		result[0]='-'//but add the minus sign to the beginning of the output
	}
	if MACOSX{
		syscall(2<<24+4,1,result,totalSize)
	}else{
		syscall(1,1,result,totalSize)
	}
	free(result)
}
writeByte(byte b){
	arr:=(byte*)malloc(1)
	arr[0]=b
	write(arr,1)
	free(arr)
}
readByte()byte{
	arr:=(byte*)malloc(1)
	read(arr,1)
	resp:=arr[0]
	free(arr)
	return resp
}
open(byte* file,long flags,long mode) int{
	if MACOSX{
		return (int)syscall(2<<24+5,file,flags,mode)
	}
	return (int)syscall(2,file,flags,mode)
}
close(int fd) int{
	if MACOSX{
		return (int)syscall(2<<24+6,fd)
	}
	return (int)syscall(3,fd)
}
writefd(int fd,byte* str, int len){
	if MACOSX{
		syscall(2<<24+4,fd,str,len)
	}else{
		syscall(1,fd,str,len)
	}
}
write(byte* buf, int num){
	writefd(1,buf,num)
}
readfd(int fd, byte* buf, int len) long{
	if MACOSX{
		return syscall(2<<24+3,fd,buf,len)
	}
	return syscall(0,fd,buf,len)
}
read(byte* buf, int num){
	readfd(0,buf,num)
}
writefdNullTerm(byte* str,int fd){
	i:=0
	for copy:=str; *copy!=(byte)0; i++{
		copy=copy+1
	}
	writefd(fd,str,i)
}
writeNullTerm(byte* str){
	writefdNullTerm(str,1)
}
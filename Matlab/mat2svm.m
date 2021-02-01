function y=mat2svm(x,z)
% we need to convert to: class 1:value1 2:value2 ...
% x is 2D array 
% It produce output text file called 'mySVMdata.txt'
% simple example to generate random x :
%      x=randi(10,20);  y=randi(2,20,1); y(y==2)=-1; x=[y x]
clc
[h w]=size(x);

for i=1:h 
    s=[];
    for j=2:w
        s=[s num2str(j-1) ':' num2str(x(i,j)) ' '];
    end    
    ss=num2str(x(i,1));
    sss=[ss ' ' s];
    y{i,1}=sss;    
end
fid = fopen(z, 'w');
for r=1:h
    fprintf(fid, '%s \n', y{r,1});    
end
fclose(fid);

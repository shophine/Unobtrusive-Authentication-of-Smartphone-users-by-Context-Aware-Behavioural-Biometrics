load ('test.mat')
load ('model.mat')
load ('label.mat')
% disp(labelVecTest)
recv=csvread('to_be_predicted.txt')
segment = recv(2)
uid = recv(1)
[predicted_label_one, accuracy_one, decision_val_one] =svmpredict(labelVecTest(segment,:),featureMatTest(segment,:), svmModel, '-b 1');
predicted_decision_value = decision_val_one(1,uid)
disp('predicted label = ')
disp(predicted_label_one)
close all
plot(featureMatTest(segment,:))
xlabel('features') 
ylabel('normalised feature values') 
fid = fopen('predicted_label.txt', 'w+');
    fprintf(fid, '%d, %.4f', predicted_label_one,predicted_decision_value);    
fclose(fid);

 
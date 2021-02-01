dataPath = 'DATASET/';
%
% 
dataUsage = 'Linear Acceleration Sensor';
fData = dir(fullfile(dataPath,strcat('*',dataUsage,'*.txt')));
RawData = cell(length(fData),12);
for i=1:length(fData)
    curAccelerationData = load(strcat(dataPath,fData(i).name));
    curRotationData = load(strcat(dataPath,strrep(fData(i).name,dataUsage,'Rotation Matrix')));
    
    
    idxID = strfind(fData(i).name,'ID');
    curID = str2double(fData(i).name(idxID+2:idxID+3));
    curGender = fData(i).name(idxID+5);
    idxSessionID = strfind(fData(i).name,'_');
    curSessionID = str2double([fData(i).name(idxID+2:idxID+3) fData(i).name(idxSessionID(2)+1:idxSessionID(3)-1)]);curOrder = str2double(fData(i).name(idxSessionID(end)+1:strfind(fData(i).name,'.txt')-1));
    
    curOrder = str2double(fData(i).name(idxSessionID(end)+1:strfind(fData(i).name,'.txt')-1));
 
    % [RawAccelerationData  RawRotationMatrixData ID Sex Session Order]
    RawData{i,1} = curAccelerationData;
    RawData{i,2} = curRotationData;
    RawData{i,3} = curID;
    RawData{i,4} = curGender;
    RawData{i,5} = curSessionID;
    RawData{i,6} = curOrder;    
end
%
%     PREPROCESSING:
% 
%    ACCELEROMETER DATA CALIBRATION
%   Input: [Raw_Acceleration_Data Rotation_Matrix]
%   Output: [Calibrated_Data] 
for i = 1: length(RawData)
   RawData{i,2} =  calibrateAccelerometerData(RawData{i,1},RawData{i,2});
end
%
%    LINEAR INTERPOLATION
%   Input: [accelerometer data]
%   Ouput: [Interpolated_accelerometer_data]

for i =1: length(RawData)
   RawData{i,2} = linearInterpolation(RawData{i,2});  
end
%
%    NOISE ELIMINATION
%   Input:   [accelerometer_data]
%   Output:  [noise_reduced_accelerometer_data]
%
for i = 1: length(RawData)
   RawData{i,2} = eliminateNoise('db6',2,RawData{i,2}); 
end

%
%    SEGMENTATION
%
%    GAIT CYCLE DETECTION
%   Input:  [accelerometer_data ]
%   Output: [peak_position]
for i = 1: length(RawData)
    peak_pos = detectGaitCycle(RawData{i,2});
    
    RawData{i,7} = peak_pos;
end
%
%   GAIT CYCLE BASED SEGMENTATION

%  Input:  [accelerometer_data peak_position]
%  Output: [1-gait cycle based segments]

for i = 1:length(RawData)
   segments = segment2GaitCycle(RawData{i,2},RawData{i,7});
    
   RawData{i,8} = segments;

end
%
%   GAIT PATTERN EXTRACTION
%  Input: [1-gait cycle based segments n p]
%  Output:[gait_patterns]
n = 4; p = 0.5;
for i=1:length(RawData)
    gait_patterns = extractGaitPattern(RawData{i,8},n,p);
    RawData{i,9} = gait_patterns;
end


%%    FEATURE EXTRACTION
%       
%   Input:  [Gait Pattern]
%   Output: [feature vectors]

for i =1:length(RawData)
   time_features = extractFeature_timedomain(RawData{i,9});
   frequency_features = extractFeature_frequencydomain(RawData{i,9});
   RawData{i,10} = time_features;
   RawData{i,11} = frequency_features;
end
%
%    FEATURE VECTOR CONCATENATION
%       
% 
selectedAxis = [3 4 5]; % use features of Z, MXY, MXYZ axes
for i =1:length(RawData)
    curTimeFeature = RawData{i,10};
    curFrequencyFeature = RawData{i,11};
    concatFeature = {};
    for ii = 1:length(curTimeFeature)
    lastTimeFeature = curTimeFeature{ii,1}(end);
    concatTimeFeature = [lastTimeFeature ];
    for iii = 1:length(selectedAxis)
        concatTimeFeature = [concatTimeFeature curTimeFeature{ii,1}(1:end-1,selectedAxis(iii))'];
    end
    %BASED ON FEATURE SELECTION
    %concatTimeFeature = [concatTimeFeature(2) concatTimeFeature(3) concatTimeFeature(1) concatTimeFeature(6:15) concatTimeFeature(4) concatTimeFeature(4+16) concatTimeFeature(4+32) concatTimeFeature(17+32)];
    %
    concatFrequencyFeature = [];
    for iii = 1:length(selectedAxis)
        concatFrequencyFeature = [concatFrequencyFeature curFrequencyFeature{ii,1}(:,selectedAxis(iii))'];
    end
    %BASED ON FEATURE SELECTION
    %concatFrequencyFeature = [ concatFrequencyFeature(41:end)];
    %
    concatFeature{ii,1} = [ concatFrequencyFeature concatTimeFeature];    
    end
    RawData{i,12} = concatFeature;
end
%
%    DATA DIVISION
%   
%  
dataTrain = RawData(mod(cell2mat(RawData(:,6)),2)==1,:);
dataTest = RawData(mod(cell2mat(RawData(:,6)),2)==0,:);
%
%    FEATURE MATRIX AND LABEL VECTOR GENERATION
%
%
% TRAINING part
featureMatTrain = [];
labelVecTrain = [];
sessionVecTrain = [];
for i = 1: length(dataTrain)
    featureMatTrain = [featureMatTrain;cell2mat(dataTrain{i,12})];
    tempVec = zeros(length(dataTrain{i,12}),1);
    tempVec(:) = dataTrain{i,3};
    labelVecTrain = [labelVecTrain; tempVec];
    tempVec = zeros(length(dataTrain{i,9}),1);
    tempVec(:) = dataTrain{i,5};
    sessionVecTrain = [sessionVecTrain;tempVec];
end

%TESTING part
featureMatTest = [];
labelVecTest = [];
sessionVecTest = [];
for i = 1: length(dataTest)
    featureMatTest = [featureMatTest;cell2mat(dataTest{i,12})];
    tempVec = zeros(length(dataTest{i,12}),1);
    tempVec(:) = dataTest{i,3};
    labelVecTest = [labelVecTest; tempVec];
    tempVec = zeros(length(dataTest{i,9}),1);
    tempVec(:) = dataTest{i,5};
    sessionVecTest = [sessionVecTest;tempVec];
    
end

%%   AUTHENTICATION
%   
%    TRAINING
%    Apply PCA to training data
[featureMatTrain,eigenVec,meanVec] = eigenGait(featureMatTrain);
%    Normalize the training data
max_val = max(featureMatTrain);
min_val = min(featureMatTrain);
max_valTrain = repmat(max_val,size(featureMatTrain,1),1);
min_valTrain = repmat(min_val,size(featureMatTrain,1),1);
featureMatTrain = ((featureMatTrain-min_valTrain)./(max_valTrain-min_valTrain) - 0.5 ) *2;
% 
%  TESTING
%
%  Apply PCA to testing data 
meanMat = repmat(meanVec,size(featureMatTest,1),1);
featureMatTest = (featureMatTest - meanMat)*eigenVec;
%  normalize feature matrix based on max min values 
max_valTest = repmat(max_val,size(featureMatTest,1),1);
min_valTest = repmat(min_val,size(featureMatTest,1),1);
featureMatTest = ((featureMatTest-min_valTest)./(max_valTest-min_valTest) - 0.5 ) *2;

%% AUTHENITCATION USING SVM 
uniLabel =unique(labelVecTrain); 
stack_x_1 = {};
stack_x_2 = {};
stack_y_1  = {};
stack_y_2  = {};
for i = 1 :length(uniLabel)
    labelVecBinTrain = labelVecTrain;
    labelVecBinTrain(labelVecBinTrain(:)~=(uniLabel(i)))=-1;
    labelVecBinTrain(labelVecBinTrain(:)==(uniLabel(i)))=1;
    unbalanced_weight = round((length(labelVecBinTrain)-sum(labelVecBinTrain(:)==1))/sum(labelVecBinTrain(:)==1));    
    model = svmtrain(labelVecBinTrain,featureMatTrain,['-t 0 -b 1 -w1 1 ' '-w-1 ' num2str(unbalanced_weight) ]);  
    labelVecBinTest = labelVecTest;
    labelVecBinTest(labelVecBinTest(:)~=uniLabel(i))=-1;
    labelVecBinTest(labelVecBinTest(:)==uniLabel(i))=1;
    [l,a,deci]=svmpredict(labelVecBinTest,featureMatTest,model);
    
%%    Consider EACH SEGMENT as a testing sample....
%    
    [val,ind] = sort(deci,'descend');
    roc_y = labelVecBinTest(ind);
	  stack_x_1tmp = cumsum(roc_y == -1);
    stack_x_2tmp = sum(roc_y == -1);
    stack_y_1tmp = (sum(roc_y == 1)-cumsum(roc_y == 1));
    stack_y_2tmp = sum(roc_y == 1);
    stack_x_1{1,i} = stack_x_1tmp;
    stack_x_2{1,i} = stack_x_2tmp;
    stack_y_1{1,i} = stack_y_1tmp;
    stack_y_2{1,i} = stack_y_2tmp;
end
%     

% %%   Consider EACH SESSIONG as a testing sample: Grouping
% %   
%     % Normalize decision value to 0..1;
%     max_deci = max(deci);
%     min_deci = min(deci);
%     deci = (deci-min_deci)/(max_deci-min_deci); 
%     % Start Grouping
%     uniSessionTest = unique(sessionVecTest);
%     newPredictedLabel = zeros(length(uniSessionTest),1);
%     newTrueLabel = zeros(length(uniSessionTest),1);
%     newDeci = zeros(length(uniSessionTest),1);
%     for ii=1:length(uniSessionTest)
%         curIdxTest = find(sessionVecTest==uniSessionTest(ii));
%         newTrueLabel(ii) = unique(labelVecBinTest(curIdxTest)); 
%         curPredictedLabel = l(curIdxTest);
%         curDecisionVal = deci(curIdxTest);
%         count_1 = sum(curPredictedLabel==1);
%         count_minus1 = sum(curPredictedLabel==-1);
%         sumDeci_1 = sum(curDecisionVal(curPredictedLabel==1));
%         sumDeci_minus1 = sum(curDecisionVal(curPredictedLabel==-1));
%         if(count_1 > count_minus1)
%             newPredictedLabel(ii) = 1;
%             newDeci(ii) = sumDeci_1/count_1;
%         elseif (count_1 < count_minus1)
%             newPredictedLabel(ii) = -1;
%             newDeci(ii) = sumDeci_minus1/count_minus1;
%         else
%             if(sumDeci_1>sumDeci_minus1)
%                 newPredictedLabel(ii) = 1;
%                 newDeci(ii) = sumDeci_1/count_1;
%             else
%                 newPredictedLabel(ii) = -1;
%                 newDeci(ii) = csumDeci_minus1/count_minus1;
%             end
%         end
%     end
%     [val,ind] = sort(newDeci,'descend');
%     roc_y = newTrueLabel(ind);
% 	  stack_x_1tmp = cumsum(roc_y == -1);
%     stack_x_2tmp = sum(roc_y == -1); 
%     stack_y_1tmp = (sum(roc_y == 1)-cumsum(roc_y == 1));
%     stack_y_2tmp = sum(roc_y == 1);
%     stack_x_1{1,i} = stack_x_1tmp;
%     stack_x_2{1,i} = stack_x_2tmp;
%     stack_y_1{1,i} = stack_y_1tmp;
%     stack_y_2{1,i} = stack_y_2tmp;
%   end
% %    


% PLOT ROC 
s = cell2mat(stack_x_1);
s2 = sum(s,2);
s3 = cell2mat(stack_x_2);
s4 = sum(s3);
stack_x = s2./s4;
s = cell2mat(stack_y_1);
s2 = sum(s,2);
s3 = cell2mat(stack_y_2);
s4 = sum(s3);
stack_y = s2./s4;
figure(1);
hold on
axis xy
axis([-0.01 1 -0.01 1])
axis equal
plot(stack_x,stack_y,'color','b');
plot([0 1], [0 1]);
xlabel('False Acceptance Rate');
ylabel('False Rejection Rate');
title(['ROC curve ']);
hold off

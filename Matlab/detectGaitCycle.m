function [ peak_pos mean_pos ] = detectGaitCycle( data)
axis_usage = 4; % the axis which will be used to distinguish gait cycle
t = 1/2; % threshold to filter insignificant peak at the first step
alpha = 1/4; % 1st user-specific value to determine starting point of gait cycle
beta = 3/4; % 2nd user-specific value to determine starting point of gait cycle
gamma = 1/6; % 3rd user-specific value to determine starting point of gait cycle

selectedData = data(:,axis_usage); 
%  Get the position of all peaks.
allPeak_pos = [];
for i = 2 : length(selectedData)-1
    if(selectedData(i) < selectedData(i-1) && selectedData(i) < selectedData(i+1))
        allPeak_pos = [allPeak_pos i];
    end
end

% Calculate the mean & std of these peaks to filter insignificant peaks
std_allPeak = std(selectedData(allPeak_pos));
mean_allPeak = mean(selectedData(allPeak_pos));
threshold = mean_allPeak + std_allPeak*t;

% Filter insignificant peaks
sigPeak_pos = [];
for i = 1 : length(allPeak_pos)
    if(selectedData(allPeak_pos(i)) < threshold)
        sigPeak_pos=[sigPeak_pos allPeak_pos(i)];
    end
end

% Calculate the auto correlation signal from the input data
auto_corr_coeff = calAutoCorrelation(selectedData);

%  Smooth the auto_correlation_coefficient 
for i = 1: 7
    auto_corr_coeff  = smooth(auto_corr_coeff);
end

%  Approximate the length of a gait cycle by selecting the 2nd peak in
% the auto correlation signal
gcLen = 0;
flag = 0;
for i=2:length(auto_corr_coeff)-1
    if(auto_corr_coeff(i) >auto_corr_coeff(i-1) && auto_corr_coeff(i) > auto_corr_coeff(i+1))
        flag = flag+1;
        if(flag ==1)
            continue;
        elseif (flag ==2)
            gcLen = i-1;
            break;
        end
    end
end

%  Extract gait cycle marks from the list of significant peaks extracted

%   Find the first candidate peak. 
i=2;
while i < length(sigPeak_pos)
    if (sigPeak_pos(i) - sigPeak_pos(i-1) < gcLen && data(sigPeak_pos(i)) < data(sigPeak_pos(i)))
        sigPeak_pos(i-1) = [];
    else
        break;
    end
end
i = 2;
while i < length(sigPeak_pos)
    if (sigPeak_pos(i)-sigPeak_pos(i-1) < alpha*gcLen)
        if (selectedData(sigPeak_pos(i)) <= selectedData(sigPeak_pos(i-1)))
            sigPeak_pos(i-1) = [];
            continue;
        else
            sigPeak_pos(i) = [];
            continue;
        end
    elseif (sigPeak_pos(i)-sigPeak_pos(i-1)<beta*gcLen)
        if (sigPeak_pos(i+1) - sigPeak_pos(i) < gamma*gcLen)
            if(selectedData(sigPeak_pos(i+1)) <= selectedData(sigPeak_pos(i)))
                sigPeak_pos(i) = [];
                continue;
            else
                sigPeak_pos(i+1) = [];
                continue;
            end
        else
            sigPeak_pos(i) = [];
            continue;
        end
    else
        i=i+1;
    end
end
if(sigPeak_pos(end)-sigPeak_pos(end-1) < beta*gcLen)
    sigPeak_pos(end) = [];
end
p1 = sigPeak_pos; p1(1) = [];
p2 = sigPeak_pos; p2(end) = [];

mean_pos = mean(p1-p2);
peak_pos  = sigPeak_pos;
function [ segment ] = segment2GaitCycle( accelerometer_data, gait_cycle_position)

    segment = {};  
    %segment data into gait cycles
  
    
    for ii=1:length(gait_cycle_position)-1
        if (gait_cycle_position(ii+1) >length(accelerometer_data))
            return;
        end
        curSegment = accelerometer_data(gait_cycle_position(ii):gait_cycle_position(ii+1),:);
        segment{ii,1} = curSegment;
    end 
    
end


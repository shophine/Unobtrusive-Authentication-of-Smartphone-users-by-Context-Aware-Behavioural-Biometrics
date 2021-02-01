function [calibrated_accelerometer_data] = calibrateAccelerometerData(data_accelerometer,data_rotation_matrix) 
    
    interpolatedData_rotation = zeros(size(data_accelerometer,1),9);
    interpolatedData_rotation(:,1) = data_accelerometer(:,1);
    %  Applying linear interpolation
    [C,ia,ic] = unique(data_rotation_matrix(:,1));
    for i = 2 : size(data_rotation_matrix,2)
        vq = interp1(data_rotation_matrix(ia,1),data_rotation_matrix(ia,i),data_accelerometer(:,1),'linear');
        interpolatedData_rotation(:,i) =  vq;
    end
    data_rotation_matrix = interpolatedData_rotation;
    y = isnan(data_rotation_matrix(:,2));
    data_rotation_matrix(y,:) = [];
    data_accelerometer(y,:) = [];

    accelerationMat = data_accelerometer(:,[2:4]);
    rotationMat = data_rotation_matrix(:,[2:10]);
    
    % Transform acceleration vectors to the ones in the fixed coordinate system
    calibrated_accelerometer_data = zeros(size(data_accelerometer));
    calibrated_accelerometer_data(:,1) = data_accelerometer(:,1);
    for i = 1 : length(accelerationMat)
       curAccVal =  accelerationMat(i,:)';
       curRotationMat = reshape(rotationMat(i,:),[3,3])';
       newAccVal = curRotationMat*curAccVal;
       calibrated_accelerometer_data(i,2:4) = newAccVal';
    end
end
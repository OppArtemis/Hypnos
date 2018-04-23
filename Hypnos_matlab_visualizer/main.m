clear variables
clc

dataPath = fullfile('.', 'data', 'hypnos-8bcfb-export.json');

%% Load raw JSON data into MATLAB format
fid = fopen(dataPath); 
raw = fread(fid,inf); 
str = char(raw'); 
fclose(fid); 
val = jsondecode(str);

jsonSleepIndices = fieldnames(val.LOG2.x846df70dbf7d2497226c8c902d8d8505.SLEEP);
% mlSleepArray = datetime(length(jsonSleepIndices), 2);
for i = 1:length(jsonSleepIndices)
    fprintf('Reading from local Firebase JSON: %u of %u\n', i, length(jsonSleepIndices));
    
    currTimeStartMs = val.LOG2.x846df70dbf7d2497226c8c902d8d8505.SLEEP.(jsonSleepIndices{i}).timeStartMs / 1000;
    currTimeEndMs = val.LOG2.x846df70dbf7d2497226c8c902d8d8505.SLEEP.(jsonSleepIndices{i}).timeEndMs / 1000;
    
    mlSleepStruct(i).startTime = datetime(currTimeStartMs, 'ConvertFrom', 'posixtime','TimeZone','UTC');
    if (currTimeEndMs == 0)
        mlSleepStruct(i).endTime = datetime('now','TimeZone','UTC');
    else
        mlSleepStruct(i).endTime = datetime(currTimeEndMs, 'ConvertFrom', 'posixtime','TimeZone','UTC');
    end
    
    mlSleepStruct(i).timeElapsed = mlSleepStruct(i).endTime - mlSleepStruct(i).startTime;
    
%     mlSleepArray(i, 1) = unix2matlab(currTimeStartMs);
%     mlSleepArray(i, 2) = unix2matlab(currTimeEndMs);
%     currTimeStartStr = datestr(currTimeStartMl);

%     fprintf('%s to %s\n', formatDateString(mlSleepStruct(i).startTime), ...
%         formatDateString(mlSleepStruct(i).endTime));
end

return

%% Organize data into table
firstTimestamp = mlSleepStruct(1).startTime;
newHour = 19;

centralTimeStamp = datetime(firstTimestamp.Year, firstTimestamp.Month, firstTimestamp.Day - 1, newHour, 0, 0);
yesterdayStartTime = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day - 1, newHour, 0, 0);
todayStartTime = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day, newHour, 0, 0);
tomorrowStartTime = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day + 1, newHour, 0, 0);

centralTimeStamp.TimeZone = 'America/Toronto';
yesterdayStartTime.TimeZone = 'America/Toronto';
todayStartTime.TimeZone = 'America/Toronto';
tomorrowStartTime.TimeZone = 'America/Toronto';

summaryArray = [];
summaryMasterInd = 1;

while todayStartTime < datetime('now', 'TimeZone','America/Toronto')
    % check each day bracket
    sleepStruct.startTime = todayStartTime;
    sleepStruct.endTime = tomorrowStartTime;
	sleepStruct.timeElapsed = 0;
        
    summaryArray{summaryMasterInd, 1} = sleepStruct;
    summarySubInd = 2;
    
    for i = 1:size(mlSleepStruct, 2)
        startTime = mlSleepStruct(i).startTime;
        endTime = mlSleepStruct(i).endTime; 
        
        if (endTime == datetime(1970, 01, 01, 'TimeZone', 'UTC'))
            endTime = datetime('now', 'TimeZone','America/Toronto');
        end
        
        % decide which times to keep
        if (startTime < yesterdayStartTime)
            % the entry is more than a day old. definitely don't keep
            continue;
        elseif (startTime < todayStartTime)
            if (endTime < todayStartTime)
                % don't keep
                continue;
            elseif (endTime > todayStartTime && endTime < tomorrowStartTime)
                % keep
            elseif (endTime > tomorrowStartTime)
                % keep
            end
        elseif (startTime > todayStartTime && startTime < tomorrowStartTime)
            if (endTime < todayStartTime)
                % error
                continue;
            elseif (endTime > todayStartTime && endTime < tomorrowStartTime)
                % keep
            elseif (endTime > tomorrowStartTime)
                % keep
            end
        elseif (startTime > tomorrowStartTime)
            if (endTime < todayStartTime)
                % error
                continue;
            elseif (endTime > todayStartTime && endTime < tomorrowStartTime)
                %error
                continue;
            elseif (endTime > tomorrowStartTime)
                % don't keep
                continue;
            end
        end
        
        sleepStruct.startTime = startTime;
        sleepStruct.endTime = endTime;
        sleepStruct.timeElapsed = endTime - startTime;
        summaryArray{summaryMasterInd, summarySubInd} = sleepStruct;
        summarySubInd = summarySubInd + 1;
    end
    
    summaryMasterInd = summaryMasterInd + 1;
    
    % update new day brackets
    centralTimeStamp = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day + 1, newHour, 0, 0);
    yesterdayStartTime = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day - 1, newHour, 0, 0);
    todayStartTime = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day, newHour, 0, 0);
    tomorrowStartTime = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day + 1, newHour, 0, 0);
    
    centralTimeStamp.TimeZone = 'America/Toronto';
    yesterdayStartTime.TimeZone = 'America/Toronto';
    todayStartTime.TimeZone = 'America/Toronto';
    tomorrowStartTime.TimeZone = 'America/Toronto';
end

% summaryArray = summaryArray(3:end, :);
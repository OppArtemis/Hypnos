function makeTable(summaryArray)
clc

for i = 1:size(summaryArray, 1)
    summaryLength = size(summaryArray(i, :), 2);
    currSummaryCol = summaryArray(i, 2:end);
    
    if isempty(currSummaryCol{1})
        continue;
    end
    
    for j0 = numel(currSummaryCol):-1:1
        if ~isempty(currSummaryCol{j0})
            break;
        end
    end
    
    tableStruct = [];
    currSummaryCol = currSummaryCol(1:j0);
    
    centralTimeStamp = summaryArray{i, 1}.startTime;
    wakeTimeThreshold = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day + 1, 7, 0, 0);
    sleepTimeThreshold = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day + 1, 19, 0, 0);
    wakeTimeThreshold.TimeZone = 'America/Toronto';
    sleepTimeThreshold.TimeZone = 'America/Toronto';
    
    
%     clc
    stage1 = 0;
    stage2 = 0;
    for indj = 1:numel(currSummaryCol)
        if stage1 == 0 && currSummaryCol{indj}.endTime > wakeTimeThreshold
            fprintf('\n');
            stage1 = 1;
        end
        
         if stage2 == 0 && currSummaryCol{indj}.endTime > sleepTimeThreshold
            fprintf('\n');
            stage2 = 1;
        end
        
        fprintf('%s to %s\n', formatDateString(currSummaryCol{indj}.startTime), formatDateString(currSummaryCol{indj}.endTime));
    end
    fprintf('---\n');
    
    % logged from the previous night
    tableStruct.timeAsleep_previousNight = ''; % when did he fall asleep?
    tableStruct.lengthTotal_previousNight = ''; % how long did he sleep for?
    tableStruct.number_previousNight = ''; % how often did he wake up? 
    tableStruct.timeAwake_previousNight = ''; % when did he wake up
    tableStruct.log_previousNight = ''; % cumulative log
    
    % when did he fall asleep?
    tableStruct.timeAsleep_previousNight = formatDateString(currSummaryCol{1}.startTime);
    
    timeSlept = 0;
    for j1 = 1:length(currSummaryCol)
%         {currSummaryCol{j1}.startTime; currSummaryCol{j1}.endTime; currSummaryCol{j1}.timeElapsed; wakeTimeThreshold}'
        
        tableStruct.log_previousNight = [tableStruct.log_previousNight ' ' ...
            formatDateString(currSummaryCol{j1}.startTime) '-' formatDateString(currSummaryCol{j1}.endTime)];
        timeSlept = timeSlept + currSummaryCol{j1}.timeElapsed;
        if currSummaryCol{j1}.endTime > wakeTimeThreshold
            break;
        end
    end
    tableStruct.lengthTotal_previousNight = formatDuration(timeSlept);
    tableStruct.number_previousNight = j1 - 1;
    
    % first wake after WAKEUP (7am)
    tableStruct.timeAwake_previousNight = formatDateString(currSummaryCol{j1}.endTime);
    
    % -----
    
    % first sleep after SLEEP (7pm)
    for j2 = length(currSummaryCol):-1:j1
%         {currSummaryCol{j2}.startTime; currSummaryCol{j2}.endTime; currSummaryCol{j2}.timeElapsed; sleepTimeThreshold}'
        
        if currSummaryCol{j2}.endTime > sleepTimeThreshold
            break;
        end
    end
    
    indsNaps = j1+1:j2-1;

    for j3 = indsNaps 
        % all these are naps
%          {currSummaryCol{j3}.startTime; currSummaryCol{j3}.endTime; currSummaryCol{j3}.timeElapsed}'
    end
    
    tableStruct.timeAsleep_nap1 = '';
    tableStruct.timeWake_nap1 = '';
    tableStruct.lengthTotal_nap1 = '';
    tableStruct.timeAsleep_nap2 = '';
    tableStruct.timeWake_nap2 = '';
    tableStruct.lengthTotal_nap2 = '';
    tableStruct.timeAsleep_napN = '';
    tableStruct.timeWake_napN = '';
    tableStruct.lengthTotal_napN = '';
    
    for indNap = 1:length(indsNaps)
        ind = indsNaps(indNap);
        switch indNap
            case 1
                % first nap (if applicable)
                tableStruct.timeAsleep_nap1 = formatDateString(currSummaryCol{ind}.startTime);
                tableStruct.timeWake_nap1 = formatDateString(currSummaryCol{ind}.endTime);
                tableStruct.lengthTotal_nap1 = formatDuration(currSummaryCol{ind}.timeElapsed);
                
            case 2
                % second nap (if applicable)
                tableStruct.timeAsleep_nap2 = formatDateString(currSummaryCol{ind}.startTime);
                tableStruct.timeWake_nap2 = formatDateString(currSummaryCol{ind}.endTime);
                tableStruct.lengthTotal_nap2 = formatDuration(currSummaryCol{ind}.timeElapsed);
                
            otherwise
                tableStruct.timeAsleep_napN = [tableStruct.timeAsleep_napN ' ' ...
                    formatDateString(currSummaryCol{ind}.startTime)];
                
                tableStruct.timeWake_napN = [tableStruct.timeAsleep_napN ' ' ...
                    formatDateString(currSummaryCol{ind}.endTime)];
                
                tableStruct.lengthTotal_napN = [tableStruct.lengthTotal_napN ' ' ...
                    formatDuration(currSummaryCol{ind}.timeElapsed)];
        end
    end
    
    
    % -----
    
    tableStruct.timeAsleep_tonightNight = formatDateString(currSummaryCol{j2}.startTime); % when did he fall asleep?
    tableStruct.lengthTotal_tonightNight = formatDuration(currSummaryCol{j2}.timeElapsed); % how long did he sleep for?
%     tableStruct.number_tonightNight = ''; % how often did he wake up?
    tableStruct.timeAwake_tonightNight = formatDateString(currSummaryCol{j2}.endTime); % when did he wake up
%     tableStruct.log_tonightNight = ''; % cumulative log
    
    % -----

    masterTable(i) = tableStruct;
    lala = 1;
%     wakeTime_previousNight{i} = formatDateString(summaryArray{i, 2}.endTime);
%     
%     for j = length(currSummaryCol):-1:3
%         if isempty(currSummaryCol{j})
%             continue
%         end
%         
%         sleepTime_todayNight{i} = formatDateString(summaryArray{i, j}.startTime);
%         wakeTime_todayNight{i} = formatDateString(summaryArray{i, j}.endTime);
%         break;
%     end

%     if ~isempty(summaryArray{i, 2})
%         sleepTime_nap1{i} = summaryArray{i, 2}.startTime;
%         wakeTime_nap1{i} = summaryArray{i, 2}.endTime;
%     end
%     
%     if ~isempty(summaryArray{i, 3})
%         sleepTime_nap2{i} = summaryArray{i, 3}.startTime;
%         wakeTime_nap2{i} = summaryArray{i, 3}.endTime;
%     end
%     
%     if ~isempty(summaryArray{i, 4})
%         sleepTime_nap3{i} = summaryArray{i, 4}.startTime;
%         wakeTime_nap3{i} = summaryArray{i, 4}.endTime;
%     end
 

end

% T = table(sleepTime_previousNight,wakeTime_previousNight,...
%     sleepTime_nap1,wakeTime_nap2,...
%     sleepTime_nap1,wakeTime_nap2,...
%     sleepTime_todayNight,wakeTime_todayNight);

T = table(masterTable);

% timeEarliest = min(mlSleepArray(:, 1));
% timeLatest = max(mlSleepArray(:, 2));
% 
% datetime
end
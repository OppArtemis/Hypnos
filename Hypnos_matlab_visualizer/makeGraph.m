function makeGraph(summaryArray)
    for i = 1:size(summaryArray, 1)
        summaryLength = size(summaryArray(i, :), 2);
        currSummaryCol = summaryArray(i, 2:end);

        centralTimeStamp = summaryArray{i, 1}.startTime;
        wakeTimeThreshold = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day, 22, 0, 0);
        sleepTimeThreshold = datetime(centralTimeStamp.Year, centralTimeStamp.Month, centralTimeStamp.Day + 1, 5, 0, 0);
        
        % format the day
        barGraphDataLength = [];
        barGraphDataLabel = {};
        currWakeTime = currSummaryCol{1}.startTime - wakeTimeThreshold;
        if currWakeTime < 0
            currWakeTime = 0;
        end
        barGraphDataLength(end+1) = hours(currWakeTime);
        barGraphDataLabel{end+1} = 'wake';
        
        for j = 1:length(currSummaryCol)-1
            if isempty(currSummaryCol{j+1})
                break;
            end
            
            % add sleep bar
            currSleepTime = currSummaryCol{j}.endTime - currSummaryCol{j}.startTime;
            barGraphDataLength(end+1) = hours(currSleepTime);
            barGraphDataLabel{end+1} = 'sleep';
            
            % add wake bar
            currWakeTime = currSummaryCol{j+1}.startTime - currSummaryCol{j}.endTime;
            barGraphDataLength(end+1) = hours(currWakeTime);
            barGraphDataLabel{end+1} = 'wake';
        end
        
        cumBarGraph = [barGraph]
        
        bar(barGraphDataLength, 'stacked');
    end
end
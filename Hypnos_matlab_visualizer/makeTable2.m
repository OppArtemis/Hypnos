function makeTable2(sleepFirebaseLog)
    wakeHour = 7;
    sleepHour = 19;
    
    startTime = sleepFirebaseLog(1).endTime;
    endTime = sleepFirebaseLog(end).endTime;
    currTimeThres = datetime(startTime.Year, startTime.Month, startTime.Day - 1, wakeHour, 0, 0);
    endTimeThres = datetime(endTime.Year, endTime.Month, endTime.Day + 1, wakeHour, 0, 0);
    currTimeThres.TimeZone = 'America/Toronto';
    endTimeThres.TimeZone = 'America/Toronto';
    
    i = 1;
    while currTimeThres < endTimeThres
        sleepLogByDay(i).today = currTimeThres;
        sleepLogByDay(i).sleeping = [];
        sleepLogByDay(i).napping = [];
        
        currTimeThres = datetime(currTimeThres.Year, currTimeThres.Month, currTimeThres.Day + 1, wakeHour, 0, 0);
        currTimeThres.TimeZone = 'America/Toronto';
        i = i + 1;
    end
    
    sleepStructList = [sleepLogByDay.today];
    
    for i = 1:numel(sleepFirebaseLog)
        startTime = sleepFirebaseLog(i).startTime;
        endtime = sleepFirebaseLog(i).endTime;
        
        todayWakeThres = datetime(endtime.Year, endtime.Month, endtime.Day, wakeHour, 0, 0);
        todaySleepThres = datetime(endtime.Year, endtime.Month, endtime.Day, sleepHour, 0, 0);
        todayWakeThres.TimeZone = 'America/Toronto';
        todaySleepThres.TimeZone = 'America/Toronto';
        
        indFound = find(sleepStructList == todayWakeThres);
        
        if startTime < todayWakeThres
            sleepLogByDay(indFound).sleeping = [sleepLogByDay(indFound).sleeping i];
        elseif endtime > todaySleepThres
            sleepLogByDay(indFound+1).sleeping = [sleepLogByDay(indFound+1).sleeping i];
        else
            sleepLogByDay(indFound).napping = [sleepLogByDay(indFound).napping i];
        end
    end
    
    for i = 1:numel(sleepLogByDay)
        todaySleepStruct = sleepLogByDay(i);
        
        sleepTableStructure = struct('date', [], ...
            'overnightStart', '', 'overnightEnd', '', 'overnightDuration', [], 'overnightCount', 0, ...
            'napsStart', '', 'napsEnd', '', 'napsDuration', [], 'napsCount', 0, ...
            'tonightStart', '', 'tonightEnd', '', 'tonightDuration', [], 'tonightCount', 0);
    
        % date representing
        sleepTableStructure.date = formatDateString(todaySleepStruct.today);
        
        % overnight sleep: start, end, total, awoken
        duration = 0;
        for j = 1:numel(todaySleepStruct.sleeping)
            switch j
                case 1
                    sleepTableStructure.overnightStart = ...
                        formatDateString(sleepFirebaseLog(todaySleepStruct.sleeping(j)).startTime);
                    
                case numel(sleepLogByDay(i).sleeping)
                    sleepTableStructure.overnightEnd = ...
                        formatDateString(sleepFirebaseLog(todaySleepStruct.sleeping(j)).endTime);
            end
            
            duration = duration + sleepFirebaseLog(todaySleepStruct.sleeping(j)).timeElapsed;
        end
        sleepTableStructure.overnightDuration = formatDuration(duration);
        sleepTableStructure.overnightCount = numel(todaySleepStruct.sleeping);
        
        % nap: start, end, total, number
        duration = 0;
        for j = 1:numel(todaySleepStruct.napping)
            sleepTableStructure.napsStart = [sleepTableStructure.napsStart ' ' ...
                formatDateString(sleepFirebaseLog(todaySleepStruct.napping(j)).startTime)];
            
            sleepTableStructure.napsEnd = [sleepTableStructure.napsEnd ' ' ...
                formatDateString(sleepFirebaseLog(todaySleepStruct.napping(j)).endTime)];
            
            duration = duration + sleepFirebaseLog(todaySleepStruct.napping(j)).timeElapsed;
        end
        sleepTableStructure.napsDuration = formatDuration(duration);
        sleepTableStructure.napsCount = numel(todaySleepStruct.napping);
        
        if i > 2
%             'tonightStart', '', 'overnightEnd', '', 'overnightDuration', [], 'overnightCount', 0);
            sleepTableStructure.tonightStart = sleepTable(i-1).overnightStart;
            sleepTableStructure.tonightEnd = sleepTable(i-1).overnightEnd;
        end
        
        sleepTable(i) = sleepTableStructure;
    end
end
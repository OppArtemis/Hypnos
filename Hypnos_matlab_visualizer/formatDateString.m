function str = formatDateString(currDatetime)

% datetime(dateHourlyLocal, 'TimeZone','America/Los_Angeles','Format','d-MMM-y HH:mm:ss Z');

    localTime = datetime(currDatetime, 'TimeZone','America/Toronto');
    str = datestr(localTime, 'mmm dd HH:MM');
end
print('js was called by nashorn!');

var callback = function(param) {
    print('js callback was called with ' + param);
}

print('WoT API is ' + WoT + ':\nversion ' + WoT.getVersion());


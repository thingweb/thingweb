print('js was called by nashorn!');

var callback = function(param) {
    print('js callback was called with ' + param);
}

print('WoT API is ' + wot + ':\nversion ' + wot.getVersion());

wot.callJava();

wot.toJava(callback);

wot.callMe(callback);
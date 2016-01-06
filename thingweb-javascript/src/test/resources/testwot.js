print('js was called by nashorn!');

print('WoT API is ' + WoT + ':\nversion ' + WoT.getVersion());

var srv = WoT.expose(testTD);
var client = WoT.consume(testTD);


srv.onUpdate("number", function(nv) {
    print("this callback saw number changing to " + nv);
});
print("added change listener");

client.setProperty("number",66)
    .then(function() {
        print("returned success");
    })
    ._catch(function(err) {
        print("got error " + err);
    });
print("setting value via client");

srv.setProperty("number",42);
print("called change on server");
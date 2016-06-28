// testTD is injected globally from the runtime
print('js was called by runtime!');

print('WoT API is ' + WoT + ':\nversion ' + WoT.getVersion());

var srv = WoT.createFromDescription(testTD);
var client = WoT.consumeDescription(testTD);


srv.then(function(thing){
    thing.onUpdateProperty("number", function(nv) {
        print("this callback saw number changing to " + nv);
    });
    print("added change listener");

    thing.setProperty("number",42);
    print("called change on server");
});

client.then(function(thing){
    client.setProperty("number",66)
        .then(function() {
            print("returned success");
        })
        ._catch(function(err) {
            print("got error " + err);
        });
    print("setting value via client");
});


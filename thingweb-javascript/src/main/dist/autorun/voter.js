 //just an example script - to be moved into other repo

 WoT.newThing("voter")
    .then(function(thing) {
        console.log("created " + thing.name);
        thing.addProperty("votes",{ "type" : "number"})
            .setProperty("votes",0)
            .onUpdateProperty("votes",
                function(newValue, oldValue) {
                    console.log(oldValue + " -> " + newValue);
                    var message = (oldValue < newValue)? "increased " : "decreased";
                    console.log("votes " + message + " to " + newValue);
                }
             );

             thing.addAction("tooCold")
             .onInvokeAction("tooCold", function() {
                console.log("vote for too cold, incrementing votes");
                var value = thing.getProperty("votes") + 1;
                thing.setProperty("votes", value);
                return value;
             });

              thing.addAction("tooHot")
              .onInvokeAction("tooHot", function() {
                 console.log("vote for too hot, decrementing votes");
                 var value = thing.getProperty("votes") - 1;
                 thing.setProperty("votes", value);
                 return value;
              });
    });
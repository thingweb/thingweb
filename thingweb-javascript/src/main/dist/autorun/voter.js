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


              return thing;
    })
    .then(function(voter) {
     WoT.discover('local', { 'name' : 'basicLed' })
      .then(function(things) {
          console.log("discover returned: " + things);

          //should be only one thing
         basicLed = things[0];

          voter.onUpdateProperty('votes', function(votes) {
            if(!basicLed) throw new Error("Led not found!")

             if(votes < 0) {
                 // make led blue
                 console.log("setting LED blue");
                 basicLed.setProperty("rgbValueRed",0);
                 basicLed.setProperty("rgbValueGreen",0);
                 basicLed.setProperty("rgbValueBlue",255);
             } else if(votes > 0) {
                 // make led red
                 console.log("setting LED red");
                 basicLed.setProperty("rgbValueRed",255);
                 basicLed.setProperty("rgbValueGreen",0);
                 basicLed.setProperty("rgbValueBlue",0);
             }  else  { // (votes == 0)
                 // make led white
                 console.log("setting LED white");
                 basicLed.setProperty("rgbValueRed",255);
                 basicLed.setProperty("rgbValueGreen",255);
                 basicLed.setProperty("rgbValueBlue",255);
             };
          });
       })
    });
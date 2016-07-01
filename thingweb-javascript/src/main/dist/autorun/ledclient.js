//just an example script - to be moved into other repo

// this script reacts to changes in the votes and changes the color of the LED accordingly

var basicLed = null;
var voter = null;

 WoT.discover('local', { 'name' : 'basicLed'})
 .then(function(things) {
    //should be only one thing
    basicLed = things[0];

    return WoT.discover('local', { 'name' : 'voter'})
 })
 .then(function(things) {
     //should be only one thing
     voter = things[0];

     voter.onUpdateProperty('votes', function(votes) {
        if(votes < 0) {
            // make led blue
            basicLed.setProperty("rgbValueRed",0);
            basicLed.setProperty("rgbValueGreen",0);
            basicLed.setProperty("rgbValueBlue",255);
        } else if(votes > 0) {
            // make led red
            basicLed.setProperty("rgbValueRed",255);
            basicLed.setProperty("rgbValueGreen",0);
            basicLed.setProperty("rgbValueBlue",0);
        }  else  { // (votes == 0)
            // make led white
            basicLed.setProperty("rgbValueRed",255);
            basicLed.setProperty("rgbValueGreen",255);
            basicLed.setProperty("rgbValueBlue",255);
        };
     });
  })


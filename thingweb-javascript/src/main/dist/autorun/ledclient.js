 //just an example script - to be moved into other repo


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
            //basicLed.setProperty(...)
        } else if(votes > 0) {
            // make led red
            //basicLed.setProperty(...)
        }  else  { // (votes == 0)
            // make led white
            //basicLed.setProperty(...)
        } else
     });
  })


let data = [];

function preload() {
    crawlerData = loadTable('cellTowersWestCoast_fixed.csv');
}

function setup() {


    var canvas = createCanvas(50, 20);
    canvas.parent('sketch-holder');
    noLoop();



    button = createButton('process');
    button.position(8, 5);
    button.mousePressed(processData);

    button1 = createButton('reload');
    button1.position(100, 5);
    button1.mousePressed(reloadPage);


}

function reloadPage() {
    document.location.reload(true);
}

function draw() {
    clear();
    for (let country of data) {
        var latlng = L.latLng(country.lat, country.lon);
        L.marker(latlng, title='test').addTo(mymap);
    }
}


function processData() {

    for (let row of crawlerData.rows) {
     rowData = row.arr[0];
     splited = split(rowData, ';');


     lat = splited[1];
     lon = splited[0];

    if(lat.localeCompare("Y") != 0){
     data.push({
        lat,
        lon
    });
}
    }
    redraw();
}


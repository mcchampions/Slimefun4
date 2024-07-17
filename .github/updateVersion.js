const fs = require('fs');

let xml = fs.readFileSync('pom.xml', {
    encoding: "utf-8",
});

xml = xml.replace("<version>UNOFFICIAL</version>",
    "<version>" + process.env.GITHUB_SHA + "</version>");
fs.writeFileSync('pom.xml', xml)
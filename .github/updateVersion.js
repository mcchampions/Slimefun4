// noinspection JSUnresolvedReference,NodeCoreCodingAssistance

const fs = require('fs');

let xml = fs.readFileSync('pom.xml', {
    encoding: "utf-8",
});
const sha = process.env.GITHUB_SHA.substring(0, 7);
xml = xml.replace("<version>UNOFFICIAL</version>",
    "<version>qs-fork-" + sha + "</version>");
fs.writeFileSync('pom.xml', xml)

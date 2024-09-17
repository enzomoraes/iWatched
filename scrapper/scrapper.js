const epsNames = [];
document.querySelectorAll(".titleCard-title_text").forEach(v=> epsNames.push(v.innerHTML))

const durations = [];
document.querySelectorAll(".titleCardList-title .duration.ellipsized").forEach(v=> durations.push(v.innerHTML))

const descriptions = [];
document.querySelectorAll(".episodeSelector-container .titleCard-synopsis.previewModal--small-text div.ptrack-content").forEach(v=> descriptions.push(v.innerHTML))

const epNumbers = [];
document.querySelectorAll(".titleCard-title_index").forEach(v=> epNumbers.push(v.innerHTML))

const epThumbs = [];
document.querySelectorAll(".episodeSelector-container .titleCard-imageWrapper > .ptrack-content > img").forEach(v=> epThumbs.push(v.src))

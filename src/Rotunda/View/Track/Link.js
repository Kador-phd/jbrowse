define(['dojo/_base/declare',
        'Rotunda/View/Track'],
       function(declare,
                Track) {

/**
 * @class
 */
return declare (Track,
{
    constructor: function(config) {
    },

    radius: 0,
    isLinkTrack: true,
    
    featureLabelFunc: function() {
	var track = this
	return function (feature) {
	    return track.label + "<br/>" + feature.seq + " (" + feature.start + ".." + feature.end + ")<br/>"
		+ feature.otherSeq + " (" + feature.otherStart + ".." + feature.otherEnd + ")"
	}
    },

    draw: function (rot, minRadius, maxRadius) {

        var featureColor = this.featureColorFunc()

        var innerRadius = rot.innerRadius()
        var featureChord = d3.svg.chord()
            .source (function (link) {
                var s = { startAngle: rot.coordToAngle (link.seq, link.start - 1),
                          endAngle: rot.coordToAngle (link.seq, link.end),
                          radius: innerRadius }
                return s
            })
            .target (function (link) {
                var t = { startAngle: rot.coordToAngle (link.otherSeq, link.otherStart - 1),
                          endAngle: rot.coordToAngle (link.otherSeq, link.otherEnd),
                          radius: innerRadius }
                return t
            })

        this.d3data (rot, function (d3data) {
	    var path = d3data.append("path")
		.attr("d", featureChord)
		.attr("fill", featureColor)
		.attr("stroke", featureColor)

	    track.addMouseover (path)
	})
    }

})
});

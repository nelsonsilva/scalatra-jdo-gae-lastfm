var LastFM=(function () {

    var urlTemplate="{USER}/recentTracks";

    var dataCallback=null;

    this.loadData = function(user) {
        var url=urlTemplate;
        url=url.replace("{USER}",user);
        $.getJSON(url,dataCallback);
    };

    return {
        getTracks : function(user,callback) {
            dataCallback=callback;
            loadData(user);
        }
    }
})();

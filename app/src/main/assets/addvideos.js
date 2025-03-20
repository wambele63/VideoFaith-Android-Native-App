$( function(){
imagetoupload = ""
    $(".imagetoggle").click( function(){
        $(".fileimg").trigger("click");
    })
window.Image =  function(input) {
    var reader = new FileReader();
    reader.onload = function(e) {
     data = e.target.result;
        $(".cropbox").fadeIn(600)
        loadImage(
         data,
            function(img){
             $('.cropbox').html(img);
                imagetoupload = img.toDataURL();
            },
            {
             meta: true,
                canvas: true,
             orientation: 1,
                maxHeight: 360
            }
        )
    }
    reader.error = function(e){
    }

    reader.readAsDataURL(input.files[0]);
}

$(".fileimg").change(function(){
    Image(this);
  });
  $(".publish").click( function(){
 var articlebody = $(".articlebody").val()
 var articlehead = $(".articlehead").val()
 var articlelocation = $(".newsloc").val()
      if(imagetoupload == "" || articlelocation == "" || articlebody == "" || articlehead ==""){
          alert("Fill all the fields and Image");
      }
      else {
          srcToFile(
            imagetoupload,
            'newsarticleimg.jpg',
            'text/plain'
        )
        .then(function(file){
        var upload = new Upload(file);
        upload.doUpload(articlehead,articlebody,articlelocation);
      }
        )
    }
  })
  function srcToFile(src, fileName, mimeType){
    return (fetch(src)
        .then(function(res){return res.arrayBuffer();})
        .then(function(buf){return new File([buf], fileName, {type:mimeType});})
    );
}
  var Upload = function (file) {
    this.file = file;
};
    Upload.prototype.getType = function() {
    return this.file.type;
};
Upload.prototype.getSize = function() {
    return this.file.size;
};
Upload.prototype.getName = function() {
    return this.file.name
};
Upload.prototype.doUpload = function (head,article,location) {
    var that = this;
    var formData = new FormData();
    alert(this.getName())
    formData.append("file",this.file);
    alert(location)
    formData.append("upload_file", true);
    formData.append("article", article);
    formData.append("location", location);
    formData.append("head", head);
    $.ajax({
        type: "POST",
        url: "http://192.168.42.87/advent/Hope/www/phpt/addnews.php",
        xhr: function () {
            var myXhr = $.ajaxSettings.xhr();
            if (myXhr.upload) {
                myXhr.upload.addEventListener('progress', that.progressHandling, false);
            }
            return myXhr;
        },
        async: true,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        timeout: 2400000
    }).done( function(data){
        alert(data)
        $(".cropbox").css("display","none")
        $(".cropbox canvas").attr("src", "")
	    $('.articlehead').val("");
	    $('.articlebody').val("");
    }).fail( function(e){
        alert("network : error")
    })
};
Upload.prototype.progressHandling = function (event) {
    var percent = 0;
    var position = event.loaded || event.position;
    var total = event.total;
    var progress_bar_id = "#progress-wrp";
    if (event.lengthComputable) {
        percent = Math.ceil(position / total * 100);
    }
    // update progressbars classes so it fits your code
    $(progress_bar_id + " .progress-bar").css("width", +percent + "%");
    $(progress_bar_id + " .status").text(percent + "%");
    alert(percent + "%")
};
window.dataURItoBlob = function(dataURI) {
    // convert base64/URLEncoded data component to raw binary data held in a string
    var byteString;
    if (dataURI.split(',')[0].indexOf('base64') >= 0)
        byteString = atob(dataURI.split(',')[1]);
    else
        byteString = unescape(dataURI.split(',')[1]);

    // separate out the mime component
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    // write the bytes of the string to a typed array
    var ia = new Uint8Array(byteString.length);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }

    return new Blob([ia], {type:mimeString});
}
})
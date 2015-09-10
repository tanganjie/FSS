/**
 * Created by sunxinwei on 8/31/15.
 */
$(document).ready(function(){
    function countTime(time){
        window.setTimeout(function(){
            time--;
            if(time >= 0){
                $("#timespan").html(time);
                countTime(time);
            }
            else{
                window.location="index.html";
            }
        }, 1000);
    }
    countTime(3);
});
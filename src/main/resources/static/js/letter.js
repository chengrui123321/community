$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	// 获取username和content
	var username = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送ajax请求
	$.post(
		CONTEXT_PATH + "/message/letter/send",
		{"username" : username, "content" : content},
		function (data) {
			// 将结果转为js对象
			data = $.parseJSON(data);
			if (data.code == 0) {
				$("#hintModalLabel").text("发送消息成功");
			} else {
				$("#hintModalLabel").text(data.msg);
			}
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
                location.reload();
			}, 2000);
		}
	);

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}
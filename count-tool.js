const fs = require('fs');
const path = require('path');

// 读取文件内容
const filePath = path.join(__dirname, 'Untitled-1.ini');
fs.readFile(filePath, 'utf8', (err, data) => {
  if (err) {
    console.error('读取文件失败:', err);
    return;
  }

  // 按行分割文件内容
  const lines = data.split(/\n|\r\n/);

  // 统计toolName出现的次数
  const totalCalls = lines.filter(line => line.includes('toolName:')).length;

  // 按顺序收集每个toolName及其对应的status
  const calls = [];
  let currentToolName = null;

  lines.forEach(line => {
    if (line.includes('toolName:')) {
      // 提取toolName的值
      currentToolName = line.split(':')[1].trim();
    } else if (line.includes('status:') && currentToolName) {
      // 提取status的值
      const status = line.split(':')[1].trim();
      calls.push({
        toolName: currentToolName,
        status: status
      });
      currentToolName = null;
    }
  });

  // 收集输出内容
  let output = `共出现${totalCalls}次调用：\n`;
  calls.forEach((call, index) => {
    output += `${index + 1}、${call.toolName} ${call.status === 'success' ? '成功' : call.status === 'failed' ? '失败' : call.status }\n`;
  });

  // 检查是否有失败的调用
  const failedCalls = calls.filter(call => call.status !== 'success');
  if (failedCalls.length > 0) {
    output += `\n以上工具调用中，以下工具调用异常或失败：\n`;
    failedCalls.forEach((call, index) => {
      output += `${index + 1}、${call.toolName}\n`;
    });
    output += `其余工具调用均成功且合理\n`;
  } else {
    output += '\n以上工具调用，全部成功且合理\n';
  }

  // 一次性输出所有内容
  console.log(output);
});
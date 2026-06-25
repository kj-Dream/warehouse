/*
 * 统一删除确认弹窗（仿 shadcn AlertDialog 风格）
 * 红底白图标 + 标题 + 描述 + 取消/确定按钮
 *
 * 使用方式：
 *   import { confirmDelete } from '@/utils/confirm'
 *   await confirmDelete('确定删除该角色吗？', '删除后不可恢复')
 *   // 用户点确定 → 继续执行后面代码
 *   // 用户点取消 → 抛出 catch，什么都不做
 */
import { h, render } from 'vue'
import { ElButton, ElDialog } from 'element-plus'

/**
 * 显示删除确认弹窗
 * @param {string} title - 弹窗标题
 * @param {string} desc - 描述文字
 * @returns {Promise} 确定 resolve，取消 reject
 */
export function confirmDelete(title, desc) {
  return new Promise((resolve, reject) => {
    const div = document.createElement('div')
    document.body.appendChild(div)

    // 关闭并清理
    const close = (result) => {
      render(null, div)
      document.body.removeChild(div)
      result ? resolve() : reject(new Error('cancel'))
    }

    const vnode = h(ElDialog, {
      modelValue: true,
      width: '420px',
      'close-on-click-modal': false,
      'onUpdate:modelValue': (val) => { if (!val) close(false) },
      // 自定义标题为空，用 body 自己画
      showClose: true
    }, {
      default: () => h('div', { style: 'display:flex;flex-direction:row;align-items:flex-start;gap:16px;padding:16px 0 80px 0;position:relative;' }, [
        // 红色圆形图标（左侧）
        h('div', { style: 'width:48px;height:48px;border-radius:50%;background:#fef2f2;display:flex;align-items:center;justify-content:center;flex-shrink:0;' }, [
          // 垃圾桶图标 SVG
          h('svg', { xmlns:'http://www.w3.org/2000/svg', width:'24', height:'24', viewBox:'0 0 24 24', fill:'none', stroke:'#ef4444', 'stroke-width':'2', 'stroke-linecap':'round', 'stroke-linejoin':'round' }, [
            h('path', { d:'M3 6h18' }),
            h('path', { d:'M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6' }),
            h('path', { d:'M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2' }),
            h('line', { x1:'10', y1:'11', x2:'10', y2:'17' }),
            h('line', { x1:'14', y1:'11', x2:'14', y2:'17' })
          ])
        ]),
        // 标题+描述（右侧，纵向排列）
        h('div', { style: 'flex:1;display:flex;flex-direction:column;gap:10px;' }, [
          h('div', { style: 'font-size:18px;font-weight:600;color:#303133;' }, title),
          h('div', { style: 'font-size:14px;color:#909399;' }, desc || '此操作不可撤销，请谨慎操作。')
        ]),
        // 按钮行（底部居中）
        h('div', { style: 'position:absolute;bottom:24px;left:50%;transform:translateX(-50%);display:flex;gap:12px;' }, [
          h(ElButton, { onClick: () => close(false), style: 'min-width:100px;' }, { default: () => '取消' }),
          h(ElButton, { type:'danger', onClick: () => close(true), style: 'min-width:100px;' }, { default: () => '确定删除' })
        ])
      ])
    })

    render(vnode, div)                                                                                                                                                
  })
}

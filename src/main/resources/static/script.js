const modules = {
  farmers: {
    title: 'Farmer Management', singular: 'Farmer', endpoint: '/api/farmers', number: 'MODULE 01',
    fields: [
      {key:'farmerName',label:'Farmer Name',type:'text',required:true,source:'farmer_name'},
      {key:'phone',label:'Phone Number',type:'tel',required:true,source:'phone'},
      {key:'village',label:'Village',type:'text',required:true,source:'village'},
      {key:'district',label:'District',type:'text',required:true,source:'district'}],
    columns:[['farmer_id','ID'],['farmer_name','Farmer Name'],['phone','Phone'],['village','Village'],['district','District']]
  },
  fields: {
    title:'Field Management',singular:'Field',endpoint:'/api/fields',number:'MODULE 02',
    fields:[{key:'farmerId',label:'Farmer',type:'select',from:'/api/farmers',id:'farmer_id',display:'farmer_name',source:'farmer_id'},
      {key:'fieldName',label:'Field Name',type:'text',required:true,source:'field_name'},
      {key:'areaAcres',label:'Area (acres)',type:'number',required:true,step:'0.01',min:'0.01',source:'area_acres'}],
    columns:[['field_id','ID'],['farmer_name','Farmer'],['field_name','Field Name'],['area_acres','Area (acres)']]
  },
  crops: {
    title:'Crop Management',singular:'Crop',endpoint:'/api/crops',number:'MODULE 03',
    fields:[{key:'cropName',label:'Crop Name',type:'text',required:true,source:'crop_name'},
      {key:'cropType',label:'Crop Type',type:'text',required:true,source:'crop_type'}],
    columns:[['crop_id','ID'],['crop_name','Crop Name'],['crop_type','Crop Type']]
  },
  seasons: {
    title:'Season Management',singular:'Season',endpoint:'/api/seasons',number:'MODULE 04',
    fields:[{key:'seasonName',label:'Season Name',type:'text',required:true,source:'season_name'},
      {key:'startDate',label:'Start Date',type:'date',required:true,source:'start_date'},
      {key:'endDate',label:'End Date',type:'date',required:true,source:'end_date'}],
    columns:[['season_id','ID'],['season_name','Season'],['start_date','Start Date'],['end_date','End Date']]
  },
  'yield-records': {
    title:'Yield Record Management',singular:'Yield Record',endpoint:'/api/yield-records',number:'MODULE 05',
    fields:[{key:'fieldId',label:'Field',type:'select',from:'/api/fields',id:'field_id',display:'field_name',source:'field_id'},
      {key:'cropId',label:'Crop',type:'select',from:'/api/crops',id:'crop_id',display:'crop_name',source:'crop_id'},
      {key:'seasonId',label:'Season',type:'select',from:'/api/seasons',id:'season_id',display:'season_name',source:'season_id'},
      {key:'yieldKg',label:'Yield (kg)',type:'number',required:true,step:'0.01',min:'0',source:'yield_kg'}],
    columns:[['yield_id','ID'],['field_name','Field'],['crop_name','Crop'],['season_name','Season'],['yield_kg','Yield (kg)']]
  }
};
let activeModule = 'farmers';
const $ = id => document.getElementById(id);
const form = $('recordForm');

async function api(url, options={}) {
  const response = await fetch(url, options);
  const type = response.headers.get('content-type') || '';
  const data = type.includes('application/json') ? await response.json() : await response.text();
  if (!response.ok) throw new Error(data.message || data || 'Request failed');
  return data;
}
function makeInput(field) {
  const label = document.createElement('label'); label.textContent = field.label;
  let input;
  if (field.type === 'select') {
    input = document.createElement('select');
    const first = document.createElement('option'); first.value=''; first.textContent=`Select ${field.label.toLowerCase()}`; input.appendChild(first);
    api(field.from).then(items => items.forEach(item => { const option=document.createElement('option'); option.value=item[field.id]; option.textContent=item[field.display]; input.appendChild(option); })).catch(() => {});
  } else {
    input = document.createElement('input'); input.type=field.type;
    if (field.step) input.step=field.step; if (field.min !== undefined) input.min=field.min;
  }
  input.name=field.key; input.id=`field-${field.key}`; input.required=!!field.required;
  label.appendChild(input); return label;
}
function drawForm() {
  const config=modules[activeModule]; $('formTitle').textContent=config.title; $('tableTitle').textContent=config.title.replace('Management','Records'); $('moduleEyebrow').textContent=config.number;
  $('saveButton').textContent=`Add ${config.singular}`; $('recordId').value=''; $('formMessage').textContent='';
  $('formFields').replaceChildren(...config.fields.map(makeInput));
  $('cancelButton').hidden=true;
}
function drawTable(items) {
  const config=modules[activeModule];
  $('tableHead').innerHTML=`<tr>${config.columns.map(c=>`<th>${c[1]}</th>`).join('')}<th>Actions</th></tr>`;
  $('recordCount').textContent=`${items.length} record${items.length===1?'':'s'}`;
  const body=$('tableBody'); body.replaceChildren();
  if (!items.length) { const tr=document.createElement('tr'); const td=document.createElement('td'); td.colSpan=config.columns.length+1; td.className='empty'; td.textContent='No records found. Add the first record using the form above.'; tr.appendChild(td); body.appendChild(tr); return; }
  items.forEach(item=>{
    const tr=document.createElement('tr');
    config.columns.forEach(([key])=>{const td=document.createElement('td'); td.textContent=item[key] ?? ''; tr.appendChild(td);});
    const actionCell=document.createElement('td');
    const edit=document.createElement('button'); edit.className='small'; edit.textContent='Edit'; edit.addEventListener('click',()=>editRecord(item));
    const del=document.createElement('button'); del.className='small danger'; del.textContent='Delete'; del.addEventListener('click',()=>deleteRecord(item));
    actionCell.append(edit,del); tr.appendChild(actionCell); body.appendChild(tr);
  });
}
async function loadRecords() {
  try { const data=await api(modules[activeModule].endpoint); drawTable(data); }
  catch(error) { $('tableBody').innerHTML=`<tr><td class="empty">${escapeText(error.message)}. Check that the PostgreSQL tables exist.</td></tr>`; }
}
function escapeText(value) { const div=document.createElement('div'); div.textContent=String(value); return div.innerHTML; }
function editRecord(item) {
  const config=modules[activeModule]; $('recordId').value=item[`${activeModule==='yield-records'?'yield':activeModule.slice(0,-1)}_id`] ?? item.farmer_id ?? item.field_id ?? item.crop_id ?? item.season_id;
  config.fields.forEach(field=>{ const input=$(`field-${field.key}`); input.value=item[field.source] ?? ''; });
  $('saveButton').textContent=`Update ${config.singular}`; $('cancelButton').hidden=false; $('formTitle').textContent=`Update ${config.singular}`; $('formMessage').textContent='Edit the details and click Update.'; window.scrollTo({top:0,behavior:'smooth'});
}
async function deleteRecord(item) {
  const config=modules[activeModule];
  const id=item[`${activeModule==='yield-records'?'yield':activeModule.slice(0,-1)}_id`] ?? item.farmer_id ?? item.field_id ?? item.crop_id ?? item.season_id;
  if (!confirm(`Delete this ${config.singular.toLowerCase()} record?`)) return;
  try { const result=await api(`${config.endpoint}/${id}`,{method:'DELETE'}); $('formMessage').textContent=result.message; await loadRecords(); }
  catch(error) { $('formMessage').textContent=error.message; }
}
form.addEventListener('submit', async event=>{
  event.preventDefault(); const config=modules[activeModule], id=$('recordId').value; const body={};
  config.fields.forEach(field=>{ const input=$(`field-${field.key}`); let value=input.value; if(field.type==='number'||field.type==='select') value=value===''?null:Number(value); body[field.key]=value; });
  try { const result=await api(id?`${config.endpoint}/${id}`:config.endpoint,{method:id?'PUT':'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)}); $('formMessage').textContent=id?`${config.singular} updated successfully.`:`${config.singular} added successfully.`; form.reset(); $('recordId').value=''; $('saveButton').textContent=`Add ${config.singular}`; $('cancelButton').hidden=true; $('formTitle').textContent=config.title; await loadRecords(); }
  catch(error) { $('formMessage').textContent=error.message; }
});
$('moduleTabs').addEventListener('click',event=>{const button=event.target.closest('button[data-module]'); if(!button)return; activeModule=button.dataset.module; document.querySelectorAll('.tab').forEach(tab=>tab.classList.toggle('active',tab===button)); form.reset(); drawForm(); loadRecords();});
$('cancelButton').addEventListener('click',()=>{form.reset(); drawForm();});
$('refreshButton').addEventListener('click',loadRecords);
$('healthButton').addEventListener('click',async()=>{try{$('healthResult').textContent=JSON.stringify(await api('/api/health'),null,2);}catch(error){$('healthResult').textContent=error.message;}});
drawForm(); loadRecords();

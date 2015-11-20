/*
 * User: eldad.Dor
 * Date: 03/02/14 17:23
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.sqlref;

import com.idi.intellij.plugin.query.annoref.config.SPViewPanelForm;

import javax.swing.*;
import java.awt.*;

/**
 * @author eldad
 * @date 03/02/14
 */
public class SPViewPanelTest {
	public static void main(String[] args) {
		JFrame f = new JFrame(SPViewPanelTest.class.getName());
		final Container c = f.getContentPane();
		c.setLayout(new BorderLayout());
//		DefaultSyntaxKit.initKit();
		final SPViewPanelForm spViewPanelForm = new SPViewPanelForm(null, null);
		final JEditorPane codeEditor = new JEditorPane();
		spViewPanelForm.getTextAreaPanel().add(codeEditor);
		c.add(spViewPanelForm.getTextAreaPanel(), BorderLayout.CENTER);
		c.doLayout();
//		codeEditor.setContentType("text/java");
		codeEditor.setContentType("text/sql");
		codeEditor.setText("\n" +
				"create proc INSERT_EVENT_TYPE_F15\n" +
				"       @user_id      SY_USER,\n" +
				"\t\t\t @unit_id      SY_UNIT\n" +
				"\n" +
				"as\n" +
				"\n" +
				"  declare   @status             int          ,\n" +
				"            @client_p           SY_CLIENT    ,\n" +
				"\t\t\t\t    @event_type_dsc     SY_TEXT\t\t\t ,\n" +
				"\t\t\t\t\t\t@policy_nr   \t\t\t\tSY_POLICY,\n" +
				"\t\t\t\t\t\t@follow_up_date     char(10) ,\n" +
				"            @follow_up_type     SY_CODE  ,\n" +
				"            @expt_event         SY_FLAG  ,\n" +
				"\t\t\t\t\t\t@event_nr           SY_SERIAL,\n" +
				"\t\t\t\t\t\t@sur_clas_code_opt  SY_CODE  ,\n" +
				"\t\t\t\t\t\t@getdate            SY_PDATE\n" +
				"\n" +
				"begin\n" +
				"\n" +
				" select @follow_up_date = convert(char(10), dateadd(day, FOLLOW_UP_DAYS,\n" +
				"                               convert(datetime,getdate(),103)),103),\n" +
				"        @follow_up_type  = FOLLOW_UP_TYPE  ,\n" +
				"        @expt_event      = EXPT_EVENT\n" +
				"   from E_EVENT_TYPE\n" +
				"  where EVENT_TYPE = 'F15'\n" +
				"\n" +
				" select @getdate  = getdate()\n" +
				" print \"start @getdate = %1! @follow_up_date = %2! \", @getdate , @follow_up_date\n" +
				"\n" +
				" declare popul_policy cursor for\n" +
				"\tselect distinct PH.POLICY_NR ,\n" +
				"\t       PH.CLIENT_P           ,\n" +
				"\t\t\t\t PH.SUR_CLASS_CODE_OPT\n" +
				"  from P_POL_HEADER PH, P_APT A\n" +
				" where PH.STS_CODE \t\t\t\t \t\t= '20'\n" +
				"\t and PH.SUPER_CLASS_CODE \t\t= '1'\n" +
				"\t and PH.CLASS_CODE \t\t\t \t\t= '2'\n" +
				"\t and PH.SUR_CLASS_CODE   \t\t= '1'\n" +
				"\t and PH.SUR_CLASS_CODE_OPT != '1'\n" +
				"\t and PH.POLICY_NR %100 = 0\n" +
				"\t and PH.POLICY_START_DATE  > '15/11/2013'\n" +
				"\t and PH.POLICY_NR           = A.POLICY_NR\n" +
				"   and A.ENDORS_NR            = PH.LAST_POLICY\n" +
				"\t and A.SUR_AMT_CONTENT   > 169000\n" +
				"\t and not exists ( select 1\n" +
				"\t                    from E_EVENT E\n" +
				"\t\t\t\t\t\t\t\t\t\t where PH.POLICY_NR = E.POLICY\n" +
				"\t\t\t\t\t\t\t\t\t\t   and E.EVENT_NR \t> 104871934--137128176\n" +
				"\t\t\t\t\t\t\t\t\t\t\t and E.EVENT_TYPE\tin ('F15' , '237','010', 'ZA1'))\n" +
				"  for read only\n" +
				"\n" +
				" \topen popul_policy\n" +
				"\n" +
				" \tfetch popul_policy\n" +
				" \t    into @policy_nr,\n" +
				" \t         @client_p ,\n" +
				" \t         @sur_clas_code_opt\n" +
				"\tif(@@sqlstatus = 2)\n" +
				"\tbegin\n" +
				"\t\tclose popul_policy\n" +
				"\t  deallocate cursor popul_policy\n" +
				"\t  return 1000\n" +
				"\tend\n" +
				" \twhile (@@sqlstatus = 0)\n" +
				" \tbegin\n" +
				"    exec @status = SP_NUMERATOR\n" +
				"\t\t     @type = 4 ,\n" +
				"\t\t     @nrt  =  @event_nr out\n" +
				"\t\t\tif (@status != 0)\n" +
				"\t\t\tbegin\n" +
				"\t\t\t  return 2000\n" +
				"\t\t\tend\n" +
				"\n" +
				"\t\tif (@sur_clas_code_opt = '2')\n" +
				"\t\tbegin\n" +
				"\t\t  select @event_type_dsc  = 'דירות תכולה'\n" +
				"\t\tend\n" +
				"\t\telse\n" +
				"\t\tbegin\n" +
				"\t\t  select @event_type_dsc  = 'דירות מבנה ותכולה'\n" +
				"\t\tend\n" +
				"\n" +
				"\t\texec @status =  SP_ADD_EVENT\n" +
				"\t\t       @event_nr       = @event_nr       ,\n" +
				"\t\t       @event_ty       = 'F15'           ,\n" +
				"\t\t       @module         = '1'             ,\n" +
				"\t\t       @event_ex       = @expt_event     ,\n" +
				"\t\t       @client         = @client_p       ,\n" +
				"\t\t       @policy         = @policy_nr      ,\n" +
				"\t\t       @event_dsc      = @event_type_dsc ,\n" +
				"\t\t       @mail_in        = 0               ,\n" +
				"\t\t       @mail_out       = 0               ,\n" +
				"\t\t       @follow_upd     = @follow_up_date ,\n" +
				"\t\t       @follow_upt     = @follow_up_type ,\n" +
				"\t\t       @response       = ''            ,\n" +
				"\t\t       @date           = ''            ,\n" +
				"\t\t       @time           = ''            ,\n" +
				"\t\t       @ref            = null            ,\n" +
				"\t\t       @start_time     = @getdate        ,\n" +
				"\t\t       @user           = @user_id        ,\n" +
				"\t\t       @unit           = @unit_id        ,\n" +
				"\t\t       @station        = null        \t\t ,\n" +
				"\t\t       @head_conf_dsc  = null        \t\t ,\n" +
				"\t\t       @head_conf_user = null        \t\t ,\n" +
				"\t\t       @follow_up_time = null\n" +
				"\t\tif (@status != 0 )\n" +
				"\t\tbegin\n" +
				"\t\t\tprint \"@policy_nr = %1! , @client_p = %2!\", @policy_nr, @client_p\n" +
				"\t\t\treturn 3000\n" +
				"\t\tend\n" +
				"\tfetch popul_policy\n" +
				" \t    into @policy_nr,\n" +
				" \t         @client_p ,\n" +
				" \t         @sur_clas_code_opt\n" +
				"\tend\n" +
				"\n" +
				"\tif(@@sqlstatus = 1)\n" +
				"\tbegin\n" +
				"\t\tclose popul_policy\n" +
				"\t  deallocate cursor popul_policy\n" +
				"\t  return 4000\n" +
				"\tend\n" +
				"\n" +
				"\tclose popul_policy\n" +
				"\tdeallocate cursor popul_policy\n" +
				" select @getdate  = getdate()\n" +
				" print \"end @getdate = %1! \", @getdate\n" +
				"return 0\n" +
				"end\n");
//		codeEditor.setText("public static void main(String[] args) {\n}");
		f.setSize(800, 600);
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
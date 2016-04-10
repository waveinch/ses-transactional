package services

/**
  * Created by unoedx on 10/04/16.
  */
object Template {
  def render(template:String,params:Map[String,String]):String = {
    params.foldLeft(template){ case (result,(param,value)) =>
      result.replaceAll("\\{\\{\\s*"+param+"\\s*\\}\\}",value)
    }
  }
}

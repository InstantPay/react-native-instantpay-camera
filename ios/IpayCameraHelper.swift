//
//  IpayCameraHelper.swift
//  InstantpayCamera
//
//  Created by Dhananjay kumar on 18/03/26.
//

import os
import Foundation


@objcMembers public class IpayCameraHelper {
    
    private init() {}
    
    static let MAIN_LOG_TAG: String = "*IpayCamera -> ";
    
    let WARNING_LOG = "WARNING_LOG"

    let ERROR_LOG = "ERROR_LOG"
    
    let INFO_LOG = "INFO_LOG"
    
    static func logPrint(classTag: String, log: String?) -> Void {
        if(log == nil){
            return
        }
        
        let fullTagName = "\(MAIN_LOG_TAG) \(classTag) \(String(describing: log))"
        
        if let isEnableLog: Bool = Bundle.main.object(forInfoDictionaryKey: "IpayCamera_Log") as! Bool? {
            if(isEnableLog){
                let logger = Logger(subsystem: "com.instantpay.ipayCamera", category: "IpayCameraHelper")
                logger.info("\(fullTagName)")
            }
        }
    }
        
    static func logPrint(logType: String, classTag: String, log: String?){
        if(log == nil){
            return
        }
        
        let fullTagName = "\(MAIN_LOG_TAG) \(classTag) \(String(describing: log))"
        
        if let isEnableLog: Bool = Bundle.main.object(forInfoDictionaryKey: "IpayCamera_Log") as! Bool? {
            if(isEnableLog){
                let logger = Logger(subsystem: "com.instantpay.ipayCamera", category: "IpayCameraHelper")
                if(logType == "WARNING_LOG"){
                    logger.warning("\(fullTagName)")
                }
                else if(logType == "ERROR_LOG"){
                    logger.error("\(fullTagName)")
                }
                else if(logType == "INFO_LOG"){
                    logger.info("\(fullTagName)")
                }
                else{
                    logger.debug("\(fullTagName)")
                }
            }
        }
    }
}



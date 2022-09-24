using System.Data;
using AutoMapper;
using DiscussionService.Filters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/students")]
    public class StudentsController : ControllerBase
    {

        private IMapper _mapper;
        private IRepositoryWrapper _repositoryWrapper;

        public StudentsController(IMapper mapper, IRepositoryWrapper repositoryWrapper)
        {
            _mapper = mapper;
            _repositoryWrapper = repositoryWrapper;
        }

        [HttpGet]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetAllStudents()
        {
            try
            {
                var students = await _repositoryWrapper.StudentRepository.GetAllStudentsAsync();
                var result = _mapper.Map<List<GetStudentDto>>(students);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("{studentId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetStudentById([FromRoute] Guid studentId)
        {
            try
            {
                var student = await _repositoryWrapper.StudentRepository.GetStudentByIdAsync(studentId);
                var result = _mapper.Map<GetStudentDto>(student);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPost]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> CreateStudent(CreateStudentDto createStudentDto)
        {
            try
            {
                Student student = _mapper.Map<Student>(createStudentDto);
                var studentExists = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(student.Email);
                if (studentExists != null)
                {
                    return Ok();
                }
                student.Id = Guid.NewGuid();
                _repositoryWrapper.StudentRepository.Create(student);
                await _repositoryWrapper.SaveAsync();
                return Created("~api/discussion/students/" + student.Id, createStudentDto);
            }
            catch (DataException ex)
            {
                return StatusCode(500, "Unable to save data");
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }

        }

        [HttpGet("questions")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetStudentQuestions()
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Student"))
                {
                    return Unauthorized("Only students can get questions.");
                }

                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var questions = await _repositoryWrapper.QuestionRepository.GetQuestionsByStudentId(student.Id);
                if (!student.Id.Equals(questions.First().StudentId))
                {
                    return Forbid("Only the author of the questions can get their questions");
                }

                var result = _mapper.Map<List<GetQuestionDto>>(questions);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("answers")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetStudentAnswers()
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Student"))
                {
                    return Forbid("Only students can get answers.");
                }
                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var answers = await _repositoryWrapper.AnswerRepository.GetAnswersByStudentId(student.Id);
                if (!student.Id.Equals(answers.First().StudentId))
                {
                    return Forbid("Only the author of the answers can get their answers");
                }
                var result = _mapper.Map<List<GetAnswerDto>>(answers);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

    }
}

// using System.Data;
// using AutoMapper;
// using DiscussionService.Filters;
// using DiscussionService.Contracts;
// using DiscussionService.Dtos;
// using DiscussionService.Models;
// using Microsoft.AspNetCore.Mvc;

// namespace DiscussionService.Controllers
// {
//     [ApiController]
//     [Route("api/discussion/students")]
//     public class StudentsController : ControllerBase
//     {

//         private IMapper _mapper;
//         private IRepositoryWrapper _repositoryWrapper;
//         private readonly IStudentsService _studentsService;

//         public StudentsController(IMapper mapper, IRepositoryWrapper repositoryWrapper, IStudentsService studentsService)
//         {
//             _mapper = mapper;
//             _repositoryWrapper = repositoryWrapper;
//             _studentsService = studentsService;
//         }

//         [HttpGet]
//         [TypeFilter(typeof(AuthorizationFilterAttribute))]
//         public async Task<IActionResult> GetAllStudents()
//         {
//             try
//             {
//                 var result = _studentsService.GetAllStudents();
//                 return Ok(result);
//             }
//             catch (Exception ex)
//             {
//                 return StatusCode(500, "Internal server error");
//             }
//         }

//         [HttpGet("{studentId}")]
//         [TypeFilter(typeof(AuthorizationFilterAttribute))]
//         public async Task<IActionResult> GetStudentById([FromRoute] Guid studentId)
//         {
//             try
//             {
//                 var result = _studentsService.GetStudentById(studentId);
//                 return Ok(result);
//             }
//             catch (Exception ex)
//             {
//                 return StatusCode(500, "Internal server error");
//             }
//         }

//         [HttpPost]
//         [ServiceFilter(typeof(ValidationFilterAttribute))]
//         public async Task<IActionResult> CreateStudent(CreateStudentDto createStudentDto)
//         {
//             try
//             {
//                 var studentId = await _studentsService.CreateStudent(createStudentDto);
//                 return Created("~api/discussion/students/" + studentId, createStudentDto);
//             }
//             catch (DataException ex)
//             {
//                 return StatusCode(500, "Unable to save data");
//             }
//             catch (Exception ex)
//             {
//                 return StatusCode(500, "Internal server error");
//             }
//         }

//         [HttpGet("questions")]
//         [TypeFilter(typeof(AuthorizationFilterAttribute))]
//         [ServiceFilter(typeof(ValidationFilterAttribute))]
//         public async Task<IActionResult> GetStudentQuestions()
//         {
//             try
//             {
//                 var userEmail = HttpContext.Items["UserEmail"] as string;
//                 var userRole = HttpContext.Items["UserRole"] as string;

//                 if (!userRole.Equals("Student"))
//                 {
//                     return Unauthorized("Only students can get questions.");
//                 }

//                 var result = _studentsService.GetStudentQuestions(userEmail);
//                 return Ok(result);
//             }
//             catch (Exception ex)
//             {
//                 return StatusCode(500, ex.Message);
//             }
//         }

//         [HttpGet("answers")]
//         [TypeFilter(typeof(AuthorizationFilterAttribute))]
//         [ServiceFilter(typeof(ValidationFilterAttribute))]
//         public async Task<IActionResult> GetStudentAnswers()
//         {
//             try
//             {
//                 var userEmail = HttpContext.Items["UserEmail"] as string;
//                 var userRole = HttpContext.Items["UserRole"] as string;

//                 if (!userRole.Equals("Student"))
//                 {
//                     return Forbid("Only students can get answers.");
//                 }
//                 var result = _studentsService.GetStudentAnswers(userEmail);
//                 return Ok(result);
//             }
//             catch (Exception ex)
//             {
//                 return StatusCode(500, ex.Message);
//             }
//         }

//     }
// }